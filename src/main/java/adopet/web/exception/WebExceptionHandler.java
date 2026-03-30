package adopet.web.exception;

import adopet.exception.DuplicateEntityException;
import adopet.exception.EntityNotFoundException;
import adopet.exception.ExternalServiceException;
import adopet.exception.InvalidStateTransitionException;
import adopet.exception.InvalidUserInputException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler({
            InvalidUserInputException.class,
            DuplicateEntityException.class,
            EntityNotFoundException.class,
            InvalidStateTransitionException.class,
            ExternalServiceException.class
    })
    public String handleBusinessException(RuntimeException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpectedException(Exception exception, Model model) {
        model.addAttribute("errorMessage", "Erro inesperado. Tente novamente mais tarde.");
        return "error";
    }
}