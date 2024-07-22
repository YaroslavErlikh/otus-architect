package hw06.ioc

import erlikh.yaroslav.hw06.generator.adapter.AdapterGenerator
import erlikh.yaroslav.hw06.generator.adapter.AdapterGeneratorImpl
import erlikh.yaroslav.hw06.generator.interfaces.IMovable
import erlikh.yaroslav.hw06.generator.ioc.IoC
import erlikh.yaroslav.hw06.generator.ioc.ScopeBasedStrategy
import erlikh.yaroslav.hw06.generator.model.Position
import erlikh.yaroslav.hw4.actions.basecommand.BaseCommand
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy
import static org.mockito.Mockito.*

class IocTest {

    private BaseModel baseModel

    @BeforeEach
    void setUp() {
        ScopeBasedStrategy scopeBasedStrategy = new ScopeBasedStrategy()
        (new ScopeBasedStrategy.InitScopeBasedIoCCommand(scopeBasedStrategy)).execute()
        baseModel = mock(BaseModel.class)
    }

    @Test
    void 'register_and_create_object_generator_adapters_single_for_one_thread'() {
        AdapterProvider adapterProvider = spy(new AdapterProvider())
        // подменяем зависимость генерации адаптеров
        ((BaseCommand) IoC.resolve("IoC.Register", "Adapter.Generator.Create", args -> adapterProvider.generate())).execute()

        assertThatThrownBy(() -> {
            IoC.resolve("Adapter.Generator")
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("Unknown key Adapter.Generator")

        IMovable movable1 = IoC.resolve("Adapter", IMovable.class, baseModel)
        AdapterGenerator adapterGenerator1 = IoC.resolve("Adapter.Generator")
        assertThat(adapterGenerator1).isInstanceOf(AdapterGeneratorImpl.class)

        IMovable movable2 = IoC.resolve("Adapter", IMovable.class, baseModel)
        AdapterGenerator adapterGenerator2 = IoC.resolve("Adapter.Generator")
        assertThat(adapterGenerator2).isInstanceOf(AdapterGeneratorImpl.class).isSameAs(adapterGenerator1)
        verify(adapterProvider, times(1)).generate()
    }

    @Test
    void 'register_and_create_object_generator_adapters_single_for_every_thread'() throws InterruptedException {
        AdapterProvider adapterProvider = spy(new AdapterProvider())
        // подменяем зависимость генерации адаптеров
        ((BaseCommand) IoC.resolve("IoC.Register", "Adapter.Generator.Create", args -> adapterProvider.generate())).execute()

        Map<Integer, AdapterGenerator> generators = new ConcurrentHashMap<>()

        Function<Integer, Runnable> runnable = i ->
                () -> {
                    IMovable movable1 = IoC.resolve("Adapter", IMovable.class, baseModel)
                    AdapterGenerator adapterGenerator1 = IoC.resolve("Adapter.Generator")
                    assertThat(adapterGenerator1).isInstanceOf(AdapterGeneratorImpl.class)
                    generators.put(i, adapterGenerator1)
                }

        // отдельные потоки
        Thread thread1 = new Thread(runnable.apply(0))
        thread1.start()

        Thread thread2 = new Thread(runnable.apply(1))
        thread2.start()

        // текущий поток
        runnable.apply(2).run()

        // ждём выполнения
        thread1.join()
        thread2.join()

        // проверка - генератор создан только один раз
        assertThat(generators.size()).isEqualTo(3)
        assertThat(generators.get(0)).isSameAs(generators.get(1)).isSameAs(generators.get(2))
        verify(adapterProvider, times(1)).generate()
    }

    @Test
    void 'generate_adapter_and_call_metods'() {
        IMovable movable = IoC.resolve("Adapter", IMovable.class, baseModel)
        ((BaseCommand) IoC.resolve("IoC.Register", "IMovable:position.get", args -> ((BaseModel) args[0]).getAttribute("position"))).execute()
        ((BaseCommand) IoC.resolve("IoC.Register", "IMovable:velocity.get", args -> ((BaseModel) args[0]).getAttribute("velocity"))).execute()
        ((BaseCommand) IoC.resolve("IoC.Register", "IMovable:position.set", args -> {
            ((BaseModel) args[0]).setAttribute("position", args[1])
            return null
        })).execute()

        Position position = new Position(1, 2, 3)

        movable.getPosition()
        verify(baseModel, times(1)).getAttribute("position")
        verify(baseModel, times(0)).getAttribute("velocity")
        verify(baseModel, times(0)).setAttribute("position", position)

        movable.getVelocity()
        verify(baseModel, times(1)).getAttribute("position")
        verify(baseModel, times(1)).getAttribute("velocity")
        verify(baseModel, times(0)).setAttribute("position", position)

        movable.setPosition(position)
        verify(baseModel, times(1)).getAttribute("position")
        verify(baseModel, times(1)).getAttribute("velocity")
        verify(baseModel, times(1)).setAttribute("position", position)
    }

    private static class AdapterProvider {
        AdapterGenerator generate() {
            return new AdapterGeneratorImpl()
        }
    }
}
