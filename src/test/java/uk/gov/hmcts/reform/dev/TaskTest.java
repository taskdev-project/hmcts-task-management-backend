package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.dev.models.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TaskTest {

    @Test
    void taskStoresItsProperties() {
        Task task = new Task();

        task.setTitle("Review case");
        task.setDescription("Check the submitted documents");
        task.setStatus("TODO");

        assertEquals("Review case", task.getTitle());
        assertEquals("Check the submitted documents", task.getDescription());
        assertEquals("TODO", task.getStatus());
        assertNull(task.getId());
        assertNull(task.getDueDate());
    }
}
