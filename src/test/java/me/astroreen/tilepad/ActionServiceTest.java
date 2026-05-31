package me.astroreen.tilepad;

import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;
import me.astroreen.tilepad.service.ActionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionServiceTest {

    private final ActionService actionService = new ActionService();

    @Test
    void canExecute_null_returnsFalse() {
        assertFalse(actionService.canExecute(null));
    }

    @Test
    void canExecute_nullValue_returnsFalse() {
        ActionConfig action = new ActionConfig(ActionType.COMMAND, null);
        assertFalse(actionService.canExecute(action));
    }

    @Test
    void canExecute_blankValue_returnsFalse() {
        ActionConfig action = new ActionConfig(ActionType.COMMAND, "   ");
        assertFalse(actionService.canExecute(action));
    }

    @Test
    void canExecute_emptyValue_returnsFalse() {
        ActionConfig action = new ActionConfig(ActionType.URL, "");
        assertFalse(actionService.canExecute(action));
    }

    @Test
    void canExecute_valid_returnsTrue() {
        ActionConfig command = new ActionConfig(ActionType.COMMAND, "echo hi");
        assertTrue(actionService.canExecute(command));

        ActionConfig url = new ActionConfig(ActionType.URL, "https://example.com");
        assertTrue(actionService.canExecute(url));

        ActionConfig app = new ActionConfig(ActionType.APP, "/usr/bin/gedit");
        assertTrue(actionService.canExecute(app));
    }

    @Test
    void execute_null_doesNotThrow() {
        assertDoesNotThrow(() -> actionService.execute(null));
    }

    @Test
    void execute_blank_doesNotThrow() {
        ActionConfig blank = new ActionConfig(ActionType.COMMAND, "");
        assertDoesNotThrow(() -> actionService.execute(blank));
    }

    @Test
    void execute_validCommand_doesNotThrow() {
        ActionConfig echo = new ActionConfig(ActionType.COMMAND, "echo tilepad-test");
        assertDoesNotThrow(() -> actionService.execute(echo));
    }
}
