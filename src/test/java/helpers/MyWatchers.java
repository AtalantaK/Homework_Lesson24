package helpers;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class MyWatchers implements TestWatcher, BeforeAllCallback, AfterAllCallback {

    static int PassedTC;
    static int FailedTC;
    static int DisabledTC;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        System.out.println("Запускаю тесты...");
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("Тест \"" + context.getDisplayName() + "\" прошел успешно");
        PassedTC++;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("Тест \"" + context.getDisplayName() + "\" упал");
        System.out.println("\tПричина падения: " + cause.getMessage());
        FailedTC++;
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.out.println("Тест \"" + context.getDisplayName() + "\" отключен."
                + "\n\tПричина: " + reason.get());
        DisabledTC++;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("Тесты завершены");
        System.out.println("Кол-во пройденных тестов: " + (PassedTC + FailedTC + DisabledTC)
                + "\nИтог:\n"
                + "\t" + PassedTC + " шт. - Passed\n"
                + "\t" + FailedTC + " шт. - Failed\n"
                + "\t" + DisabledTC + " шт. - Disabled\n");
    }

}
