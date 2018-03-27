package com.oni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Scanner;


/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
	static Scanner scanner = new Scanner(System.in);
    /**
     * @param args
     */
    public static void main( String[] args )
    {
		SpringApplication.run(App.class , args);
    }
    private static void pilihan() {
    	System.out.println("1. Streaming + Indexing");
    	System.out.println("2. Cari");
    	System.out.println("Masukkan pilihan :");
    	int pilihan = scanner.nextInt();
    	switch(pilihan) {
    	case 1:
    		index();
    		pilihan();
    		break;
    	case 2:
    		cari();
    		pilihan();
    		break;
    	default:
    		System.out.println("Pilihan anda salah");
    		pilihan();
    	}
    }
    
    private static void index(){
    	Streaming stream = new Streaming();
    }
    
    private static void cari() {
    	String keyword;
		System.out.print("Masukkan keyword: ");
		keyword = scanner.nextLine();
		Searcher s = new Searcher();
		try {
			s.doSearching(keyword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
