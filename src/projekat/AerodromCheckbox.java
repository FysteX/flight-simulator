package projekat;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.HeadlessException;
import java.awt.Label;

public class AerodromCheckbox extends Checkbox {

	Aerodrom aerodrom;
	
	public AerodromCheckbox(boolean state, Aerodrom aerodrom) {
		super("", state);
		this.aerodrom = aerodrom;
	}
	
	public Aerodrom getAerodrom() {
		return aerodrom;
	}

}
