package swingApp;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import reyes.Carta;
import reyes.Ficha;

public class PanelTableroSeleccion extends JPanel {

	private static final long serialVersionUID = 6840011839081352510L;
	private CountDownLatch latchCartaElegida = new CountDownLatch(1);
	private Carta cartaElegida;
	public static volatile int idCartaElegida;
	private double escala;
	private int largoPanel;
	private int altoPanel;
	
	public PanelTableroSeleccion(List<Carta> cartasAElegir, int largoPanel, int altoPanel) {
		this.largoPanel=largoPanel;
		this.altoPanel=altoPanel;
		this.setBackground(new Color(0x614828));
		int tamFicha = GUI.getTAM_FICHA();
		double altoFicha=altoPanel/4;
		escala=altoFicha/tamFicha;
		int tamFichaEscalado=(int) (tamFicha*escala);
		int centradoLargo=(largoPanel/2)-tamFichaEscalado;
		setLayout(null);
		for (int i = 0, x = 0; i < cartasAElegir.size(); i++, x++) {
			Ficha[] fichasActuales = new Ficha[2];
			if(cartasAElegir.get(i)!=null) {
				cartasAElegir.get(i).setDefault();
				 fichasActuales= cartasAElegir.get(i).getFichas();				
			}else {
				fichasActuales[0]=null;
				fichasActuales[1]=null;
			}
			for (int j = 0, y = 0; j < 2; j++, y++) {
				PanelFicha ficha = new PanelFicha(fichasActuales[j], y, x,escala,escala);
				ficha.setBounds((y * tamFichaEscalado)+centradoLargo, x * tamFichaEscalado, tamFichaEscalado, tamFichaEscalado);
				ficha.setBackground(new Color(0x614828));
				ficha.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(ficha.getFicha() != null)
							onCartaElegida(ficha.getFicha().getCarta());
					}
				});
				this.add(ficha);
			}
		}
	}

	public CountDownLatch getLatchCartaElegida() {
		return latchCartaElegida;
	}
	public Carta getCartaElegida() {
		return cartaElegida;
	}

	public void setLatchCartaElegida(CountDownLatch countDownLatch) {
		latchCartaElegida = countDownLatch;
	}
	
	protected void onRotarCartaClick() {
		cartaElegida.rotarCarta();
		onCartaElegida(cartaElegida);
		Sonido.playCartaSeleccionada();
	}

	public synchronized void onCartaElegida(Carta carta) {
		if(!GUI.isTurnoActual())
			return;
		this.cartaElegida = carta;
		idCartaElegida = carta.getId();
		this.removeAll();
		

		int tamFicha= GUI.getTAM_FICHA();
		int tamFichaEscalado=(int) (tamFicha*escala);
		int centradoLargo=(largoPanel/2)-tamFichaEscalado;
		int centradoAlto=(altoPanel/2)-tamFichaEscalado;


		Ficha[] fichas=carta.getFichas();
		int rotacion=fichas[0].getRotacion();
		
		setLayout(null);
		
		int x1 = centradoLargo,y1=centradoAlto,x2=centradoLargo,y2=centradoAlto;
		switch (rotacion) {
		case 1:
			x1+=0;
			y1+=0;
			x2+=tamFichaEscalado;
			y2+=0;
			break;
		case 2:
			x1+=0;
			y1+=0;
			x2+=0;
			y2+=tamFichaEscalado;
			break;
		case 3:
			x1+=tamFichaEscalado;
			y1+=0;
			x2+=0;
			y2+=0;
			break;
		case 4:
			x1+=0;
			y1+=tamFichaEscalado;
			x2+=0;
			y2+=0;
			break;

		default:
			break;
		}

		PanelFicha ficha1 = new PanelFicha(fichas[0], 0, 0,escala,escala);
		ficha1.setBorder(BorderFactory.createLineBorder(Color.orange,5));
		ficha1.setBounds(x1,y1, tamFichaEscalado, tamFichaEscalado);
		this.add(ficha1);
		
		PanelFicha ficha2 = new PanelFicha(fichas[1], 0, 0,escala,escala);
		ficha2.setBounds(x2,y2, tamFichaEscalado, tamFichaEscalado);
		this.add(ficha2);
		ImageIcon rotarIcon = new ImageIcon("./assets/rotar.png");
		JButton boton = new JButton(rotarIcon);
		
		boton.setBounds(0, 3 * tamFichaEscalado, tamFichaEscalado * 2, tamFichaEscalado);
		this.add(boton);
		this.setBackground(new Color(0x614828));

		boton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onRotarCartaClick();
			}
		});
		this.repaint();
		latchCartaElegida.countDown();
	}

	public void setCartaElegida(Carta c) {
		cartaElegida = c;
	}

}
