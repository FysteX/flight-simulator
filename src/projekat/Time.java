package projekat;

public class Time {
	
	int minut;
	int sat;
	boolean vremeSeUvecavaFlag;

	public Time(int sat, int minut) {
		this.sat = sat;
		this.minut = minut;
	}
	
	public boolean daLiSeVremeUvecava() {
		return vremeSeUvecavaFlag;
	}
	
	public void vremeSeUvecava() {
		vremeSeUvecavaFlag = true;
	}

	public void vremeSeNeUvecava() {
		vremeSeUvecavaFlag = true;
	}
	
	public void setMinut(int minut) {
		this.minut = minut;
	}

	public void setSat(int sat) {
		this.sat = sat;
	}

	public int getMinut() {
		return minut;
	}

	public int getSat() {
		return sat;
	}

	public void inkrementirajVreme() {
		minut++;
		if(minut == 60) {
			minut = 0;
			sat++;
		}
		if(sat == 24) {
			sat = 0;
		}
	}
	
	
	 public void uvecajVreme(int minuti){
		 for(int i = 0 ; i < minuti ; i++) {
			 inkrementirajVreme();
		}
	 }
	
	@Override
	public String toString() {
		return String.format("%02d", sat) + ":" + String.format("%02d", minut);
	}
	
	//1 ako je this > time | 0 ako je this = tim | -1 ako je this < time
	public int compare(Time time)
	{
		if(this.sat > time.getSat()) {
			return 1;
		}else if(this.sat < time.getSat()) {
			return -1;
		} else {
			if(this.minut > time.getMinut()) {
				return 1;
			}
			else if(this.minut < time.getMinut()) {
				return -1;
			}
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Time)) {
			return false;
		}
		if(((Time)obj).getSat() == sat && ((Time)obj).getMinut() == minut) {
			return true;
		}
		return false;
	}
	
	public synchronized Time clone() {
		return new Time(this.sat, this.minut);
	}
}
