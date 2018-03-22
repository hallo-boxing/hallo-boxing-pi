package org.xiaoheshan.hallo.boxing.pi.looper.executor;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xiaoheshan.hallo.boxing.pi.io.ConsoleHolder;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author : _Chf
 * @since : 03-20-2018
 */
@Component
public class DoorExecutor implements IExecutor<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoorExecutor.class);

    private static final String PARAM_OPEN = "1";
    private static final String PARAM_CLOSE = "0";
    private GpioPinDigitalOutput doorPin;
    private final ConsoleHolder console;

    @Autowired
    public DoorExecutor(ConsoleHolder console) {
        this.console = console;
    }

    @PostConstruct
    public void init() {
//        setDynamic();
        initPin();
    }

    private void setDynamic() {
        System.setProperty("pi4j.linking", "dynamic");
    }

    private void initPin() {
        doorPin = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_22, "DOOR", PinState.HIGH);
    }

    private void openDoor() throws InterruptedException {
        doorPin.high();
        TimeUnit.MILLISECONDS.sleep(500);
    }

    private void closeDoor() throws InterruptedException {
        doorPin.low();
        TimeUnit.MILLISECONDS.sleep(500);
    }

    @Override
    public String execute(String... parameters) {
        Assert.notNull(parameters, "参数不能为null");
        Assert.isTrue(parameters.length == 1, "参数只有一个");
        console.println("正在执行柜门命令: " + Arrays.toString(parameters));
        LOGGER.info("正在执行柜门命令: " + Arrays.toString(parameters));
        try {
            if (PARAM_OPEN.equals(parameters[0])) {
                openDoor();
            }
            else {
                closeDoor();
            }
            console.println("完成执行柜门命令");
            return "OK";
        } catch (InterruptedException e) {
            LOGGER.error("控制柜门发送错误", e);
            return "ERROR";
        }
    }

}
