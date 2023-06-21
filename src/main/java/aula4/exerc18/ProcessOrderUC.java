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
//Classe do exercicio proposto, identificar os nós
    public int[] process(Order order) {
        var errors = validator.validateBasicData(order);
        if (!errors.isEmpty()) {//Nó (1)
            var errorMsg = String.join(",", errors);
            throw new IllegalArgumentException(errorMsg);
        }
        if (service.isDown() || emailSender.isOffline()) { //Nó (2 - Serv...) e Nó (3 - email...)
            throw new RuntimeException("Services offline. Try again later.");
        }
        int orderedProds = 0, unorderedProds = 0;
        for (int prodId : order.getProdIds()) { //Nó (5)
            var success = repo.orderProduct(prodId);
            if (success) { //Nó (6)
                orderedProds++;
            } else {
                unorderedProds++; //Nó (7)
            }
        }
        var transportId = service.makeTag(order.getCode(), order.getAddress());
        var emailId = emailSender.sendEmail(order.getEmail(), "Your order", order.getDesc());
        int[] ret = {transportId, emailId, orderedProds, unorderedProds};
        return ret; //Nó (8)
    }
}
