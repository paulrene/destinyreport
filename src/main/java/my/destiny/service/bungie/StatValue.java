package my.destiny.service.bungie;

import lombok.Getter;
import lombok.Setter;
import my.destiny.service.bungie.type.ModeType;
import my.destiny.service.bungie.type.StatType;

@Getter
@Setter
public class StatValue {

    private ModeType modeType;
    private StatType statType;
    private String displayValue;
    private long value;

}
