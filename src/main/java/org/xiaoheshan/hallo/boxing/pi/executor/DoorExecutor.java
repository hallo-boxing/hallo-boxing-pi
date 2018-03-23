package org.xiaoheshan.hallo.boxing.pi.executor;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiaoheshan.hallo.boxing.pi.util.ConsoleUtil;
import org.xiaoheshan.hallo.boxing.pi.util.ThreadSleepUtil;

import java.util.Arrays;

import static org.xiaoheshan.hallo.boxing.pi.enums.ErrorCodeEnum.DOOR_PARAM_ERROR;
import static org.xiaoheshan.hallo.boxing.pi.enums.ResponseEnum.ERROR;
import static org.xiaoheshan.hallo.boxing.pi.enums.ResponseEnum.OK;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
public class DoorExecutor implements IExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoorExecutor.class);

    private static final String PARAM_OPEN = "1";
    private static final String PARAM_CLOSE = "0";

    private GpioPinDigitalOutput doorPin;

    public DoorExecutor() {
        doorPin = GpioFactory.getInstance()
                .provisionDigitalOutputPin(RaspiPin.GPIO_03, "DOOR", PinState.HIGH);
    }

    @Override
    public String execute(String... params) {
        if (params == null || params.length != 1) {
            LOGGER.error(DOOR_PARAM_ERROR.getName());
            return ERROR.getWithParam(DOOR_PARAM_ERROR.getCode().toString());
        }
        ConsoleUtil.get().println("正在执行柜门命令，参数：" + Arrays.toString(params));
        switch (params[0]) {
            case PARAM_OPEN:
                openDoor();
                break;
            case PARAM_CLOSE:
                closeDoor();
                break;
            default:
                ConsoleUtil.get().println("执行柜门命令参数错误");
                return ERROR.getWithParam(DOOR_PARAM_ERROR.getCode().toString());
        }
        ConsoleUtil.get().println("执行柜门命令完成");
        return OK.get();
    }

    private void openDoor() {
        doorPin.high();
        ThreadSleepUtil.sleep(500);
    }

    private void closeDoor() {
        doorPin.low();
        ThreadSleepUtil.sleep(500);
    }
}
