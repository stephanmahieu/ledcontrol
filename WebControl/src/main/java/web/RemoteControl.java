package web;

import ledcontrol.serialcomm.processor.SerialDataSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/control")
public class RemoteControl {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteControl.class);
    private static final int HTTP_OK = 200;

    @Autowired
    private SerialDataSender sendSerial;


    @RequestMapping(method= RequestMethod.GET)
    public String remote() {
        return "/control/debug";
    }

    @RequestMapping(value = "/setDateTime", method=RequestMethod.GET)
    public String setDateTime(Model model) {
        return "/control/setDateTime";
    }


    @RequestMapping(value = "effect/{type}", method=RequestMethod.GET)
    public void effectAction(HttpServletRequest req, HttpServletResponse resp, @PathVariable String type) {
        sendSerial.sendEffect(type);
    }

    @RequestMapping(value = "button/{command}", method=RequestMethod.GET)
    public void buttonAction(HttpServletRequest req, HttpServletResponse resp, @PathVariable String command) {
        LOG.info("RemoteController received command [{}] from web, dispatching to Arduino...", command);

        if ("debugOn".equalsIgnoreCase(command)) {
            sendSerial.sendDebugState(true);
        }
        else if ("debugOff".equalsIgnoreCase(command)) {
            sendSerial.sendDebugState(false);
        }
        else if ("reset".equalsIgnoreCase(command)) {
            sendSerial.sendReset();
        }
//		else if ("getDateTime".equalsIgnoreCase(command)) {
//			sendGetDateTime(TO_UNIT);
//		}
        else if ("status".equalsIgnoreCase(command)) {
            sendSerial.sendStatusRequest();
        }
        else if ("command".equalsIgnoreCase(command)) {
            sendSerial.sendCommand(command, null);
        }

        // response to ajax request
        resp.setStatus(HTTP_OK);
    }

    @RequestMapping(value = "dim/{value}", method=RequestMethod.GET)
    public void dimAction(HttpServletRequest req, HttpServletResponse resp, @PathVariable String value) {
        LOG.info("RemoteController received dim value [{}] from web, dispatching to Arduino...", value);

        List<String> param = new ArrayList<>();
        param.add(value);
        sendSerial.sendCommand("brightness", param);

        // response to ajax request
        resp.setStatus(HTTP_OK);
    }

}
