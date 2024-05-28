package hw06.adapter

import erlikh.yaroslav.hw06.generator.adapter.AdapterGeneratorImpl
import erlikh.yaroslav.hw4.actions.interfaces.Movable
import erlikh.yaroslav.hw4.model.basemodel.BaseModel
import org.joor.Reflect
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.lang.reflect.Field

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock

class AdapterGeneratorTest {

    private AdapterGeneratorImpl adapterGenerator
    private BaseModel baseModel

    @BeforeEach
    void setUp() {
        adapterGenerator = new AdapterGeneratorImpl()
        baseModel = mock(BaseModel.class)
    }

    @Test
    void 'use_cache_generation_adapters'() throws NoSuchFieldException, IllegalAccessException {
        Field adaptersField = AdapterGeneratorImpl.class.getDeclaredField("adapters")
        adaptersField.setAccessible(true)
        Map<String, Reflect> adapters = (Map<String, Reflect>) adaptersField.get(adapterGenerator)

        Movable movable1 = adapterGenerator.resolve(Movable.class, baseModel)
        Reflect reflect1 = adapters.get(Movable.class.getName())

        Movable movable2 = adapterGenerator.resolve(Movable.class, baseModel)
        Reflect reflect2 = adapters.get(Movable.class.getName())
        assertThat(reflect2).isSameAs(reflect1)
    }

    @Test
    void 'generation_concrete_adapter'() {
        Movable movable = adapterGenerator.resolve(Movable.class, baseModel)
        assertThat(movable.getClass().getName()).isEqualTo("erlikh.yaroslav.hw06.generator.adapter.MovableAdapter")
        assertThat(movable.getClass().getDeclaredMethods().length).isEqualTo(3)
    }
}
