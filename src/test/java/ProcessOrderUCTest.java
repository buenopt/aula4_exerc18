import aula4.exerc18.Order;
import aula4.exerc18.ProcessOrderUC;
import aula4.exerc18.Repository;
import aula4.exerc18.Validator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessOrderUCTest {

    @Test
    void testProcess_ValidateBasicData_Success() {
        // Caminho: 1-2-3
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        when(validatorMock.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(repoMock.orderProduct(anyInt())).thenReturn(true);
        when(serviceMock.isDown()).thenReturn(false);
        when(emailSenderMock.isOffline()).thenReturn(false);

        int[] result = processOrderUC.process(order);

        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        assertEquals(0, result[2]);
        assertEquals(0, result[3]);
    }

    @Test
    void testProcess_ValidateBasicData_ValidationError() {
        // Caminho: 1-2-4
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        List<String> errors = new ArrayList<>();
        errors.add("Error 1");
        errors.add("Error 2");

        when(validatorMock.validateBasicData(order)).thenReturn(errors);

        assertThrows(IllegalArgumentException.class, () -> {
            processOrderUC.process(order);
        });
    }

    @Test
    void testProcess_ValidateBasicData_ServiceDown() {
        // Caminho: 1-2-3-5-6
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        when(validatorMock.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(repoMock.orderProduct(anyInt())).thenReturn(true);
        when(serviceMock.isDown()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            processOrderUC.process(order);
        });
    }

    @Test
    void testProcess_ValidateBasicData_EmailSenderOffline() {
        // Caminho: 1-2-3-5-7
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        when(validatorMock.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(repoMock.orderProduct(anyInt())).thenReturn(true);
        when(serviceMock.isDown()).thenReturn(false);
        when(emailSenderMock.isOffline()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            processOrderUC.process(order);
        });
    }

    @Test
    void testProcess_OrderProduct_Success() {
        // Caminho: 1-2-3-8-9
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        when(validatorMock.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(repoMock.orderProduct(anyInt())).thenReturn(true);
        when(serviceMock.isDown()).thenReturn(false);
        when(emailSenderMock.isOffline()).thenReturn(false);

        int[] result = processOrderUC.process(order);

        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        assertEquals(1, result[2]);
        assertEquals(0, result[3]);
    }

    @Test
    void testProcess_OrderProduct_Failure() {
        // Caminho: 1-2-3-8-10
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        when(validatorMock.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(repoMock.orderProduct(anyInt())).thenReturn(false);
        when(serviceMock.isDown()).thenReturn(false);
        when(emailSenderMock.isOffline()).thenReturn(false);

        int[] result = processOrderUC.process(order);

        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        assertEquals(0, result[2]);
        assertEquals(1, result[3]);
    }

    @Test
    void testProcess_MakeTag_SendEmail_Success() {
        // Caminho: 1-2-3-8-9-11-12
        Validator validatorMock = mock(Validator.class);
        Repository repoMock = mock(Repository.class);
        ProcessOrderUC processOrderUC = new ProcessOrderUC(validatorMock, repoMock);
        Order order = new Order(1, "buenopt@hotmail.com", "Exercicio18", "Endereco");

        when(validatorMock.validateBasicData(order)).thenReturn(new ArrayList<>());
        when(repoMock.orderProduct(anyInt())).thenReturn(true);
        when(serviceMock.isDown()).thenReturn(false);
        when(emailSenderMock.isOffline()).thenReturn(false);
        when(serviceMock.makeTag(anyInt(), anyString())).thenReturn(100);
        when(emailSenderMock.sendEmail(anyString(), anyString(), anyString())).thenReturn(200);

        int[] result = processOrderUC.process(order);

        assertNotNull(result);
        assertEquals(100, result[0]);
        assertEquals(200, result[1]);
        assertEquals(1, result[2]);
        assertEquals(0, result[3]);
    }
}
