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
    // [1]: Validação dos dados básicos do pedido e tratamento de erros.
    var errors = validator.validateBasicData(order);
    if (!errors.isEmpty()) {
        // [2]: Verificação se há erros na validação dos dados básicos do pedido.
        var errorMsg = String.join(",", errors);
        // [3]: Tratamento de erros: Concatenação das mensagens de erro em uma única string.
        throw new IllegalArgumentException(errorMsg);
        // [4]: Lançamento da exceção IllegalArgumentException com a mensagem de erro concatenada.
    }

    // [5]: Verificação do status dos serviços (TransportService e EmailSender).
    if (service.isDown() || emailSender.isOffline()) {
        // [6]: Verificação se o serviço de transporte está offline.
        // [7]: Verificação se o serviço de envio de email está offline.
        throw new RuntimeException("Services offline. Try again later.");
        // [8]: Lançamento da exceção RuntimeException indicando que os serviços estão offline.
    }

    int orderedProds = 0, unorderedProds = 0;
    // [9]: Processamento do pedido, incluindo a chamada para repo.orderProduct e os retornos associados.
    for (int prodId : order.getProdIds()) {
        var success = repo.orderProduct(prodId);
        if (success) {
            // [11]: Verificação se o produto foi pedido com sucesso.
            orderedProds++;
        } else {
            // [12]: Verificação se o produto não foi pedido com sucesso.
            unorderedProds++;
        }
        // [10]: Verificação se há mais produtos no pedido.
    }

    // [13]: Geração do transportId e emailId.
    var transportId = service.makeTag(order.getCode(), order.getAddress());
    var emailId = emailSender.sendEmail(order.getEmail(), "Your order", order.getDesc());

    int[] ret = {transportId, emailId, orderedProds, unorderedProds};

    // [14]: Retorno do array ret.
    return ret;
}
}
