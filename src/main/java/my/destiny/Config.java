package my.destiny;

import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class Config {

    @Parameter(
            names = { "--port" },
            description = "The port of the web server",
            required = false
    )
    private int port = 8080;

    @Parameter(
            names = { "--bungieApiKey" },
            description = "The Bungie API KEY",
            required = true
    )
    private String bungieApiKey;

}
