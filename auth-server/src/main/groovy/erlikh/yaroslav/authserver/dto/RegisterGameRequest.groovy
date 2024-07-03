package erlikh.yaroslav.authserver.dto

class RegisterGameRequest {

    private List<String> users

    RegisterGameRequest() {
    }

    RegisterGameRequest(List<String> users) {
        this.users = users
    }

    List<String> getUsers() {
        return users
    }

    RegisterGameRequest setUsers(List<String> users) {
        this.users = users
        return this
    }
}