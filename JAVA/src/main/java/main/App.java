package main;

import menu.Menu;

public class App {

	public static void main(String[] args) {
		Menu menu = new Menu();
        
        try {
            menu.exibirMenu();
        } catch (Exception e) {
            System.err.println("Erro critico na execucao do sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

	}


