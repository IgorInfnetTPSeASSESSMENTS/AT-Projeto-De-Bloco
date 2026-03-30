package adopet.web.exception;

import adopet.exception.DuplicateEntityException;
import adopet.exception.EntityNotFoundException;
import adopet.exception.InvalidUserInputException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebExceptionHandlerTest {

    private final WebExceptionHandler handler = new WebExceptionHandler();

    @Test
    void shouldRenderErrorViewForBusinessException() {
        WebExceptionHandler handler = new WebExceptionHandler();
        ConcurrentModel model = new ConcurrentModel();

        String view = handler.handleBusinessException(new InvalidUserInputException("Erro de validação."), model);

        assertEquals("error", view);
        assertEquals("Erro de validação.", model.getAttribute("errorMessage"));
    }

    @Test
    void shouldHandleInvalidUserInputException() {
        Model model = new ConcurrentModel();

        String viewName = handler.handleBusinessException(
                new InvalidUserInputException("Entrada inválida."),
                model
        );

        assertEquals("error", viewName);
        assertEquals("Entrada inválida.", model.getAttribute("errorMessage"));
    }

    @Test
    void shouldHandleDuplicateEntityException() {
        Model model = new ConcurrentModel();

        String viewName = handler.handleBusinessException(
                new DuplicateEntityException("Já existe um abrigo com esse nome."),
                model
        );

        assertEquals("error", viewName);
        assertEquals("Já existe um abrigo com esse nome.", model.getAttribute("errorMessage"));
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        Model model = new ConcurrentModel();

        String viewName = handler.handleBusinessException(
                new EntityNotFoundException("Abrigo não encontrado."),
                model
        );

        assertEquals("error", viewName);
        assertEquals("Abrigo não encontrado.", model.getAttribute("errorMessage"));
    }

    @Test
    void shouldHandleUnexpectedException() {
        Model model = new ConcurrentModel();

        String viewName = handler.handleUnexpectedException(
                new RuntimeException("Falha inesperada"),
                model
        );

        assertEquals("error", viewName);
        assertEquals("Erro inesperado. Tente novamente mais tarde.", model.getAttribute("errorMessage"));
    }
}