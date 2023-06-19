package aula4.exerc18;

public class ProcessOrderUC {
    private Validator validator;
    private TransportService service;
    private EmailSender emailSender;
    private Repository repo;

    public ProcessOrderUC(Validator validator, Repository repo) {
        this.validator = validator;
        this.repo = repo;
    }

    public void setService(TransportService service) {
        this.service = service;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public int[] process(Order order) {
        var errors = validator.validateBasicData(order);
        if (!errors.isEmpty()) {
            var errorMsg = String.join(",", errors);
            throw new IllegalArgumentException(errorMsg);
        }
        if (service.isDown() || emailSender.isOffline()) {
            throw new RuntimeException("Services offline. Try again later.");
        }
        int orderedProds = 0, unorderedProds = 0;
        for (int prodId : order.getProdIds()) {
            var success = repo.orderProduct(prodId);
            if (success) {
                orderedProds++;
            } else {
                unorderedProds++;
            }
        }
        var transportId = service.makeTag(order.getCode(), order.getAddress());
        var emailId = emailSender.sendEmail(order.getEmail(), "Your order", order.getDesc());
        int[] ret = {transportId, emailId, orderedProds, unorderedProds};
        return ret;
    }
}
