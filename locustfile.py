from locust import HttpUser, between, task


class AdopetUser(HttpUser):
    wait_time = between(1, 3)

    @task(4)
    def home(self):
        self.client.get("/", name="GET /")

    @task(2)
    def shelters(self):
        self.client.get("/shelters", name="GET /shelters")

    @task(1)
    def invalid_shelter_form(self):
        self.client.post(
            "/shelters",
            data={
                "name": "",
                "phoneNumber": "",
                "email": "invalido",
            },
            name="POST /shelters invalid",
        )
