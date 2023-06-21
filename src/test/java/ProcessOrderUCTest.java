import aula4.exerc18.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProcessOrderUCTest {
    private ProcessOrderUC processOrderUC;

    @Mock
    private Validator validator;

    @Mock
    private Repository repository;

    @Mock
    private TransportService transportService;

    @Mock
    private EmailSender emailSender;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        processOrderUC = new ProcessOrderUC(validator, repository);
        processOrderUC.setService(transportService);
        processOrderUC.setEmailSender(emailSender);
    }

    @Test
    public void testeValidoEmailSucesso() {
        Order order = new Order(1, "buenopt@hotmail.com", "Teste de pedido", "Endereço Teste");
        List<Integer> prodIds = new ArrayList<>();
        prodIds.add(1);
        prodIds.add(2);
        order.getProdIds().addAll(prodIds);

        when(validator.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(transportService.isDown()).thenReturn(false);
        when(emailSender.isOffline()).thenReturn(false);
        when(repository.orderProduct(1)).thenReturn(true);
        when(repository.orderProduct(2)).thenReturn(true);
        when(transportService.makeTag(order.getCode(), order.getAddress())).thenReturn(123);
        when(emailSender.sendEmail(order.getEmail(), "Seu pedido", order.getDesc())).thenReturn(456);

        int[] expectedResult = { 123, 456, 2, 0 };
        int[] result = processOrderUC.process(order);

        assertArrayEquals(expectedResult, result);
        verify(validator).validateBasicData(order);
        verify(transportService).isDown();
        verify(emailSender).isOffline();
        verify(repository).orderProduct(1);
        verify(repository).orderProduct(2);
        verify(transportService).makeTag(order.getCode(), order.getAddress());
        verify(emailSender).sendEmail(order.getEmail(), "Seu pedido", order.getDesc());
    }

    @Test
    public void processoPedidoInvalido() {
        Order order = new Order(1, "buenopt@hotmail.com", "Teste de pedido", "Endereço Teste");
        List<String> errors = new ArrayList<>();
        errors.add("Invalid email");
        errors.add("Invalid address");

        when(validator.validateBasicData(order)).thenReturn(errors);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            processOrderUC.process(order);
        });

        assertEquals("Invalid email,Invalid address", exception.getMessage());
        verify(validator).validateBasicData(order);
        verifyNoInteractions(transportService);
        verifyNoInteractions(emailSender);
        verifyNoInteractions(repository);
    }

    @Test
    public void processoServicoInativo() {
        Order order = new Order(1, "buenopt@hotmail.com", "Teste de pedido", "Endereço Teste");

        when(validator.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(transportService.isDown()).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processOrderUC.process(order);
        });

        assertEquals("Services offline. Try again later.", exception.getMessage());
        verify(validator).validateBasicData(order);
        verify(transportService).isDown();
        verifyNoInteractions(emailSender);
        verifyNoInteractions(repository);
    }

    @Test
    public void processoRementeEmailOffline() {
        Order order = new Order(1, "buenopt@hotmail.com", "Teste de pedido", "Endereço Teste");

        when(validator.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(transportService.isDown()).thenReturn(false);
        when(emailSender.isOffline()).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            processOrderUC.process(order);
        });

        assertEquals("Services offline. Try again later.", exception.getMessage());
        verify(validator).validateBasicData(order);
        verify(transportService).isDown();
        verify(emailSender).isOffline();
        verifyNoInteractions(repository);
    }

    @Test
    public void processoAlgunsProdutosPedidos() {
        Order order = new Order(1, "buenopt@hotmail.com", "Teste de pedido", "Endereço Teste");
        List<Integer> prodIds = new ArrayList<>();
        prodIds.add(1);
        prodIds.add(2);
        order.getProdIds().addAll(prodIds);

        when(validator.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(transportService.isDown()).thenReturn(false);
        when(emailSender.isOffline()).thenReturn(false);
        when(repository.orderProduct(1)).thenReturn(true);
        when(repository.orderProduct(2)).thenReturn(false);
        when(transportService.makeTag(order.getCode(), order.getAddress())).thenReturn(123);
        when(emailSender.sendEmail(order.getEmail(), "Seu pedido", order.getDesc())).thenReturn(456);

        int[] expectedResult = { 123, 456, 1, 1 };
        int[] result = processOrderUC.process(order);

        assertArrayEquals(expectedResult, result);
        verify(validator).validateBasicData(order);
        verify(transportService).isDown();
        verify(emailSender).isOffline();
        verify(repository).orderProduct(1);
        verify(repository).orderProduct(2);
        verify(transportService).makeTag(order.getCode(), order.getAddress());
        verify(emailSender).sendEmail(order.getEmail(), "Seu pedido", order.getDesc());
    }
}
