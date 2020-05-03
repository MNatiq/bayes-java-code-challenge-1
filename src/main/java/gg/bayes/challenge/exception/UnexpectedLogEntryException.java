package gg.bayes.challenge.exception;

public class UnexpectedLogEntryException extends RuntimeException{
    private String message;

    public UnexpectedLogEntryException(String message) {
        super(message);
        this.message = message;
    }
}
