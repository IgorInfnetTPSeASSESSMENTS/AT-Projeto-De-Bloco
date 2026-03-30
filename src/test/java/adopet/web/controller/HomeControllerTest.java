package adopet.web.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomeControllerTest {

    @Test
    void shouldReturnHomeView() {
        HomeController controller = new HomeController();

        String viewName = controller.home();

        assertEquals("home", viewName);
    }
}