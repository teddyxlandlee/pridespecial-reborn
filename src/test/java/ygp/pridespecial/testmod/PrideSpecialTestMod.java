package ygp.pridespecial.testmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ygp.pridespecial.BlankFlagShapeFactory;
import ygp.pridespecial.SingleColorFlagShapeFactory;

public class PrideSpecialTestMod {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        var shapeType = BlankFlagShapeFactory.getShapeType();
        LOGGER.info("BlankFlag shape: {}", shapeType);
        shapeType = SingleColorFlagShapeFactory.getShapeType();
        LOGGER.info("SingleColor shape: {}", shapeType);

        var flag = SingleColorFlagShapeFactory.newFlag(0x017B92);   // color of ocean
        LOGGER.info("The ocean flag: {}", flag);
    }
}
