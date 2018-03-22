package org.xiaoheshan.hallo.boxing.pi.io;

import com.pi4j.util.Console;
import org.springframework.stereotype.Component;

/**
 * @author : _Chf
 * @since : 03-16-2018
 */
@Component
public class ConsoleHolder {

    private Console console;

    public ConsoleHolder() {
        this.console = new Console();
        this.console.title("<--  Hallo Boxing -->");
        this.console.promptForExit();
    }

    public void box(String... lines) {
        this.console.box(lines);
    }

    public void println(String line) {
        this.console.println(line);
    }

    public void print(String data) {
        this.console.print(data);
    }

    public boolean isRunning() {
        return console.isRunning();
    }

}
