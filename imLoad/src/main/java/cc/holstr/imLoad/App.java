package cc.holstr.imLoad;

import cc.holstr.imLoad.gui.Window;
import cc.holstr.imLoad.unpack.Unpacker;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Unpacker.unpack();
        Window w = new Window();
        w.build();
    }
}
