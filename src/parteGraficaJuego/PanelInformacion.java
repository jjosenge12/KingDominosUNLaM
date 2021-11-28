package parteGraficaJuego;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import clasesLogicas.Jugador;
import clasesLogicas.Tablero;

public class PanelInformacion extends JPanel {
	
	private int i = 0;
	private static final long serialVersionUID = -6580547458892714155L;
	private JTextPane info;
	private List<JButton> botones;
	private List<Jugador> jugadores;
	private JPanel panelBotones; 
	private JButton btnRendirse;
	private JButton btnPuntajesDinastia;
	private GUI ventana;

	PanelInformacion(GUI ventana, List<Jugador> jugadores, int largoPanel, int altoPanel) {
		this.ventana = ventana;
		this.jugadores = jugadores;
		
		setLayout(new BorderLayout());
		info = new JTextPane();
		info.setEditable(false);
		info.setBackground(new Color(0xE3BB86));
		info.setForeground(Color.BLACK);
		info.setFont(new Font("Arial", Font.CENTER_BASELINE, 15));
		
		StyledDocument doc = info.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		this.add(info, BorderLayout.CENTER);

		panelBotones = new JPanel();
		botones = new ArrayList<JButton>();
		panelBotones.setLayout(new GridLayout(jugadores.size() + 1, 1));
		btnRendirse = new JButton("Rendirse");
		panelBotones.add(btnRendirse);
		btnRendirse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rendirse();
			}
		});
		while (i < jugadores.size()) {
			JButton boton = new JButton("Tablero " + (i + 1) + ": 0 puntos");
			botones.add(boton);
			boton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mostrarPuntaje(e.getSource());
				}
			});
			panelBotones.add(boton);
			i++;
		}
		panelBotones.setPreferredSize(new Dimension(largoPanel, altoPanel / 4));
		this.add(panelBotones, BorderLayout.SOUTH);
	}

	private void rendirse() {
		ventana.salirDeVentana();
	}

	protected void mostrarPuntaje(Object object) {
		String texto = ((JButton) object).getText();
		int indice = Character.getNumericValue(texto.charAt(8));

		Tablero tablero = jugadores.get(indice - 1).getTablero();
		String puntaje = tablero.puntajeTotal(this);
		JOptionPane.showMessageDialog(this, puntaje, "Puntaje", JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostrarInfo(String string) {
		info.setText(string);
	}

	public void deshabilitarBotones() {
		for (JButton boton : botones) {
			boton.setEnabled(false);
		}
	}

	public void habilitarBotones() {
		for (JButton boton : botones) {
			boton.setEnabled(true);
		}
	}

	public void setTextoBtnRendirse(String texto) {
		btnRendirse.setText(texto);
	}

	public void actualizarPuntaje(int indice) {
		Tablero tablero = jugadores.get(indice).getTablero();
		int puntaje = tablero.puntajeTotal(null, 0);
		JButton boton = botones.get(indice);
		boton.setText("Tablero " + (indice + 1) + ": " + puntaje + " puntos");
	}

	public void initBtnPuntajesAcumulados() {
		btnPuntajesDinastia=new JButton("Ver puntajes Acumulados:");
		btnPuntajesDinastia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ventana.mostrarPuntajesAcumulados();
			}
		});
		panelBotones.setLayout(new GridLayout(jugadores.size()+2,1));
		panelBotones.add(btnPuntajesDinastia);
	}

}
