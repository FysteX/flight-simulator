package projekat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Aplikacija extends Frame {

	private Scena scena = new Scena(this);
	private Podaci podaci = new Podaci(this);
	private Timer timer = new Timer(this);
	
	private GreskaDialog aplikacijaSeGasi;
	
	private Panel listaAerodromaGUI = new Panel(); 
	
	public void dodajUListuAerodromaGUI(Aerodrom aerodrom) {
		Panel aerodromPanel = new Panel(new FlowLayout());
		
		Label label = new Label(aerodrom.getNaziv() + "," + aerodrom.getKod() + "," + aerodrom.getX() + "," + aerodrom.getY());
		
		aerodromPanel.add(label);
		
		AerodromCheckbox checkbox = new AerodromCheckbox(true, aerodrom);
		
		checkbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				akcijaIzvrsena();
				Aerodrom aerodrom = ((AerodromCheckbox)e.getItemSelectable()).getAerodrom();
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					aerodrom.show();
				} else if(e.getStateChange() == ItemEvent.DESELECTED) {
					try {
						aerodrom.unshow();
					} catch (AerodromJeSelektovanException exception) {
						((AerodromCheckbox)e.getItemSelectable()).setState(true);
						new GreskaDialog(Aplikacija.this, "Ne mozete ukloniti selektovan aerodrom sa prikaza", ModalityType.APPLICATION_MODAL);
					}
				}
				scena.repaint();
			}
		
		});
		
		aerodromPanel.add(checkbox);
		listaAerodromaGUI.add(aerodromPanel);
		pack();
	}
	
	public Timer getTimer() {
		return this.timer;
	}
	
	public Scena getScena() {
		return this.scena;
	}
	
	public Podaci getPodaci() {
		return this.podaci;
	}

	private class AerodromWindow extends Dialog {

		private TextField nazivTextField = new TextField(10);
		private TextField kodTextField = new TextField(3);
		private TextField xTextField = new TextField(2);
		private TextField yTextField = new TextField(2);

		private String porukaGreske;

		Button dodajButton = new Button();

		public AerodromWindow(Frame owner) {
			super(owner);
			setModalityType(ModalityType.APPLICATION_MODAL);
			setBounds(owner.getX() + owner.getHeight() / 3, owner.getY() + owner.getWidth() / 3, 0, 0);
			setTitle("Aerodom");
			
			nazivTextField.addKeyListener(new KeyAdapter () {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			kodTextField.addKeyListener(new KeyAdapter () {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			xTextField.addKeyListener(new KeyAdapter () {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			yTextField.addKeyListener(new KeyAdapter () {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});

			Panel glavniPanel = new Panel(new GridLayout(0, 1));

			Panel nazivPanel = new Panel();
			nazivPanel.add(new Label("Naziv aerodroma"));
			nazivPanel.add(nazivTextField);

			Panel kodPanel = new Panel();
			kodPanel.add(new Label("Kod aerodroma"));
			kodPanel.add(kodTextField);

			Panel koordinatePanel = new Panel();
			koordinatePanel.add(new Label("Koordinate"));
			koordinatePanel.add(new Label("X"));
			koordinatePanel.add(xTextField);
			koordinatePanel.add(new Label("Y"));
			koordinatePanel.add(yTextField);

			dodajButton.addActionListener((ae) -> {
				akcijaIzvrsena();
				try {
					String naziv = nazivTextField.getText();
					String kod = kodTextField.getText();
					String x = xTextField.getText();
					String y = yTextField.getText();

					if (x.length() == 0 || y.length() == 0 || naziv.length() == 0 || kod.length() == 0) {
						throw new PraznoPoljeException();
					}

					podaci.dodajAerodrom(naziv, kod, x, y);

					dispose();
				} catch (NumberFormatException | PogresnoUnetKodException | PraznoPoljeException
						| KoordinateVanOpsegaException | AerodromSaIstimKodomPostoji | AerodromSaIstimKoordinatamaPostoji e) {
					if (e instanceof NumberFormatException) {
						porukaGreske = new String("Uneli ste vrednost za koordinate koja nije broj.");
					} else if (e instanceof PogresnoUnetKodException) {
						porukaGreske = new String("Pogresno unet kod aerodroma. Kod mora imati 3 velika slova.");
					} else if (e instanceof PraznoPoljeException) {
						porukaGreske = new String("Morate uneti sva polja.");
					} else if (e instanceof KoordinateVanOpsegaException) {
						porukaGreske = new String("Koordinate moraju imati vrednosti izmedju -90 i 90.");
					} else if (e instanceof AerodromSaIstimKodomPostoji) {
						porukaGreske = new String("Uneli ste kod aerodroma koji vec postoji.");
					} else if (e instanceof AerodromSaIstimKoordinatamaPostoji) {
						porukaGreske = new String("Uneli ste kooordinate aerodroma koji vec postoji.");
					}
					new GreskaDialog(Aplikacija.this, porukaGreske, ModalityType.APPLICATION_MODAL);
				}
			});

			Panel buttonPanel = new Panel();
			dodajButton.setLabel("Dodaj");
			buttonPanel.add(dodajButton);

			glavniPanel.add(nazivPanel);
			glavniPanel.add(kodPanel);
			glavniPanel.add(koordinatePanel);
			glavniPanel.add(buttonPanel);

			add(glavniPanel);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					akcijaIzvrsena();
					dispose();
				}
			});
			pack();
			setVisible(true);
		}

	}

	private class LetWindow extends Dialog {

		private TextField pocetniTextField = new TextField(3);
		private TextField krajnjiTextField = new TextField(3);
		private TextField satPoletanjaTextField = new TextField(2);
		private TextField minutPoletanjaTextField = new TextField(2);
		private TextField trajanjeLetaTextField = new TextField(2);

		private String porukaGreske;

		Button dodajButton = new Button();

		public LetWindow(Frame owner) {
			super(owner);
			setModalityType(ModalityType.APPLICATION_MODAL);
			setBounds(owner.getX() + owner.getHeight() / 3, owner.getY() + owner.getWidth() / 3, 0, 0);
			setTitle("Let");

			//dodavanje listenera za restartovanje tajmera
			pocetniTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			krajnjiTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			satPoletanjaTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			minutPoletanjaTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			trajanjeLetaTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					akcijaIzvrsena();
				}
			});
			
			Panel glavniPanel = new Panel(new GridLayout(0, 1));

			Panel pocetniAerodromPanel = new Panel();
			pocetniAerodromPanel.add(new Label("Kod pocetnog aerodroma"));
			pocetniAerodromPanel.add(pocetniTextField);

			Panel krajnjiAerodromPanel = new Panel();
			krajnjiAerodromPanel.add(new Label("Kod krajnjeg aerodroma"));
			krajnjiAerodromPanel.add(krajnjiTextField);

			Panel vremePoletanjaPanel = new Panel();
			vremePoletanjaPanel.add(new Label("Vreme poletanja"));
			vremePoletanjaPanel.add(new Label("Sat"));
			vremePoletanjaPanel.add(satPoletanjaTextField);
			vremePoletanjaPanel.add(new Label("Minut"));
			vremePoletanjaPanel.add(minutPoletanjaTextField);

			Panel trajanjeLetaPanel = new Panel();
			trajanjeLetaPanel.add(new Label("Trajanje leta u minutima"));
			trajanjeLetaPanel.add(trajanjeLetaTextField);

			dodajButton.addActionListener((ae) -> {
				akcijaIzvrsena();
				try {
					String kodPocetnogAerodrom = pocetniTextField.getText();
					String kodKrajnjegAerodrom = krajnjiTextField.getText();
					String satPoletanja = satPoletanjaTextField.getText();
					String minutPoletanja = minutPoletanjaTextField.getText();
					String trajanjeLeta = trajanjeLetaTextField.getText();

					if (satPoletanja.length() == 0 || minutPoletanja.length() == 0 || trajanjeLeta.length() == 0
							|| kodPocetnogAerodrom.length() == 0 || kodKrajnjegAerodrom.length() == 0) {
						throw new PraznoPoljeException();
					}

					podaci.dodajLet(kodPocetnogAerodrom, kodKrajnjegAerodrom, satPoletanja, minutPoletanja,
							trajanjeLeta);

					dispose();
				} catch (NumberFormatException | PogresnoUnetKodException | PraznoPoljeException
						| LoseUnetoVremeException | UnetiIstiAerodromiException | PocetniAerodromNePostojiException
						| KrajnjiAerodromNePostojiException e) {
					if (e instanceof NumberFormatException) {
						porukaGreske = new String("Uneli ste vrednost za vreme koja nije broj.");
					} else if (e instanceof PogresnoUnetKodException) {
						porukaGreske = new String("Pogresno unet kod aerodroma. Kod mora imati 3 velika slova.");
					} else if (e instanceof PraznoPoljeException) {
						porukaGreske = new String("Morate uneti sva polja.");
					} else if (e instanceof LoseUnetoVremeException) {
						porukaGreske = new String("Uneli ste neadekvatno vreme.");
					} else if (e instanceof UnetiIstiAerodromiException) {
						porukaGreske = new String("Uneli ste isti aerodrom kao pocetni i krajnji.");
					} else if (e instanceof PocetniAerodromNePostojiException) {
						porukaGreske = new String("Uneli ste pocetni aerodrom koji ne postoji.");
					} else if (e instanceof KrajnjiAerodromNePostojiException) {
						porukaGreske = new String("Uneli ste krajnji aerodrom koji ne postoji.");
					}
					new GreskaDialog(Aplikacija.this, porukaGreske, ModalityType.APPLICATION_MODAL);
				}
			});

			Panel buttonPanel = new Panel();
			dodajButton.setLabel("Dodaj");
			buttonPanel.add(dodajButton);

			glavniPanel.add(pocetniAerodromPanel);
			glavniPanel.add(krajnjiAerodromPanel);
			glavniPanel.add(vremePoletanjaPanel);
			glavniPanel.add(trajanjeLetaPanel);
			glavniPanel.add(buttonPanel);

			add(glavniPanel);

			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					akcijaIzvrsena();
					dispose();
				}
			});
			pack();
			setVisible(true);
		}
	}
	
	public void vremeIsticePrikaz() {
		aplikacijaSeGasi = new GreskaDialog(this, "Aplikacija se gasi za 5 sekunde, uradite neku akciju kako se ne bi ugasila.", ModalityType.MODELESS);	
	}
	
	//funkcija restartuje tajmer i gasi prozor koji obavestava da se aplikacija gasi za 5 sekunde
	public void akcijaIzvrsena() {
		timer.restartujShutDownTajmer();
		if(aplikacijaSeGasi != null) {
			aplikacijaSeGasi.dispose();
			aplikacijaSeGasi = null;
		}
	}

	public void gasiAplikaciju() {
		dispose();
		timer.interrupt();
	}
	
	public void populateWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gasiAplikaciju();
			}
		});
		
		// dodavanje menija
		MenuBar meniTraka = new MenuBar();

		Menu dodajMeni = new Menu();
		dodajMeni.setLabel("Dodaj");
		
		MenuItem aerodromItem = new MenuItem();
		MenuItem letItem = new MenuItem();
		
		aerodromItem.addActionListener((ae) -> {
			akcijaIzvrsena();
		});
		letItem.addActionListener((ae) -> {
			akcijaIzvrsena();
		});
		
		aerodromItem.addActionListener((ae) -> {
			new AerodromWindow(this);
		});

		letItem.addActionListener((ae) -> {
			new LetWindow(this);
		});

		aerodromItem.setLabel("Aerodrom");
		letItem.setLabel("Let");

		dodajMeni.add(aerodromItem);
		dodajMeni.add(letItem);
		meniTraka.add(dodajMeni);

		Menu citanjeIzFajlaMeni = new Menu();
		citanjeIzFajlaMeni.setLabel("Procitaj iz fajla");

		MenuItem citanjeAerodromaItem = new MenuItem();
		MenuItem citanjeLetovaItem = new MenuItem();

		citanjeAerodromaItem.setLabel("Aerodrome");
		citanjeLetovaItem.setLabel("Letove");

		citanjeAerodromaItem.addActionListener((ae) -> {
			akcijaIzvrsena();
			try {
				podaci.procitajAerodromeIzFajla();
			} catch (PogresnoUnetKodException | LosBrojKolonaException | KoordinateVanOpsegaException
					| AerodromSaIstimKodomPostoji | AerodromSaIstimKoordinatamaPostoji | FileNotFoundException | CitanjeIzFajlaException e) {
				String porukaGreske = new String();
				if (e instanceof LosBrojKolonaException) {
					porukaGreske = new String("U fajlu sa aerodromima, svaki red mora imati 4 kolone.");
				} else if (e instanceof PogresnoUnetKodException) {
					porukaGreske = new String("U fajlu sa aerodromima, svaki kod aerodroma mora imati 3 velika slova.");
				} else if (e instanceof KoordinateVanOpsegaException) {
					porukaGreske = new String("U fajlu sa aerodromima, koordinate moraju biti izmedju -90 i 90.");
				} else if (e instanceof AerodromSaIstimKodomPostoji) {
					porukaGreske = new String("U fajlu sa aerodromima postoji aerodrom koji ima isti kod kao postojeci aerodrom u sistemu.");
				} else if (e instanceof AerodromSaIstimKoordinatamaPostoji) {
					porukaGreske = new String("U fajlu sa aerodromima postoji aerodrom koji ima iste koordinate kao postojeci aerodrom u sistemu.");
				} else if (e instanceof FileNotFoundException) {
					porukaGreske = new String("Fajl sa aerodromima ne posotji");
				} else if (e instanceof CitanjeIzFajlaException) {
					porukaGreske = new String("Neuspesno citanje iz fajla sa aerodromima");
				}
				new GreskaDialog(Aplikacija.this, porukaGreske, ModalityType.APPLICATION_MODAL);
			}
		});

		
		  citanjeLetovaItem.addActionListener((ae) -> { 
			  akcijaIzvrsena();
			  try {
				  podaci.procitajLetoveIzFajla();
				  } catch (PogresnoUnetKodException | LoseUnetoVremeException | UnetiIstiAerodromiException |
						  PocetniAerodromNePostojiException | KrajnjiAerodromNePostojiException | 
							LosBrojKolonaException | FileNotFoundException | CitanjeIzFajlaException e) 
			  	  {
					  String porukaGreske = new String();
					  if (e instanceof PogresnoUnetKodException) {
						  porukaGreske = new String("U fajlu sa letovima, svaki kod aerodroma mora imati 3 velika slova.");
					  } else if (e instanceof LoseUnetoVremeException) {
						  porukaGreske = new String("U fajlu sa letovima, neki let ima neadekvatno vreme");
					  } else if (e instanceof UnetiIstiAerodromiException) {
						  porukaGreske = new String("U fajlu sa letovima, neki let ima isti kod i za krajnji i za pocetni aerodrom");
					  } else if (e instanceof PocetniAerodromNePostojiException) {
						  porukaGreske = new String("U fajlu sa letovima, neki let ima pocetni aerodrom koji ne postoji");
					  } else if (e instanceof KrajnjiAerodromNePostojiException) {
						  porukaGreske = new String("U fajlu sa letovima, neki let ima krajnji aerodrom koji ne postoji");
					  } else if (e instanceof LosBrojKolonaException) {
						  porukaGreske = new String("U fajlu sa letovima, svaki red mora da ima 5 kolona");
					  }else if (e instanceof FileNotFoundException) {
						  porukaGreske = new String("Fajl sa letovima ne postoji");
					  } else if (e instanceof CitanjeIzFajlaException) {
						  porukaGreske = new String("Neuspesno citanje iz fajla sa letovima");
					  }
					  new GreskaDialog(Aplikacija.this, porukaGreske, ModalityType.APPLICATION_MODAL);
				  } 
			  });

		citanjeIzFajlaMeni.add(citanjeAerodromaItem);
		citanjeIzFajlaMeni.add(citanjeLetovaItem);
		  
		Menu cuvanjeUFajlMeni = new Menu();
		cuvanjeUFajlMeni.setLabel("Sacuvaj podatke");
		
		MenuItem cuvanjeItem = new MenuItem();
		cuvanjeItem.setLabel("Sacuvaj");
		
		cuvanjeItem.addActionListener((ae) -> {
			akcijaIzvrsena();
			try {
				podaci.upisiPodatkeUFajlove();
			} catch (FileNotFoundException e) {
				new GreskaDialog(this, "Fajlovi ne postoje", ModalityType.APPLICATION_MODAL);
			}
		});
		
		cuvanjeUFajlMeni.add(cuvanjeItem);
		
		meniTraka.add(citanjeIzFajlaMeni);
		meniTraka.add(cuvanjeUFajlMeni);
		
		Menu simulacijaMeni = new Menu();
		simulacijaMeni.setLabel("Simulacija");
		
		MenuItem zapocniSimulacijuItem = new MenuItem();
		zapocniSimulacijuItem.setLabel("Zapocni simulaciju");
		
		zapocniSimulacijuItem.addActionListener((ae) -> {
			timer.zapocniSimulaciju();
		});
		
		simulacijaMeni.add(zapocniSimulacijuItem);
		
		meniTraka.add(simulacijaMeni);
		
		setMenuBar(meniTraka);
		
		scena.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				podaci.selectAerodrom(e.getX(), e.getY());
				akcijaIzvrsena();
			}	
		});
		
		add(scena, BorderLayout.CENTER);
		
		listaAerodromaGUI.setLayout(new GridLayout(0, 1));
		listaAerodromaGUI.setBackground(Color.lightGray);
		
		add(listaAerodromaGUI, BorderLayout.WEST);

	}

	public Aplikacija() {
		super("Aplikacija");

		setBounds(400, 100, 0, 0);
		
		populateWindow();

		// repaint();

		pack();
		setVisible(true);
		timer.start();

	}

	public static void main(String[] args) {
		new Aplikacija();
	}

}
