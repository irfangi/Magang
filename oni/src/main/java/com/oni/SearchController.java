package com.oni;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/twitter")
public class SearchController {
    @Autowired
    static Searcher searcher = new Searcher();

    @GetMapping("/cari/{kata}")
    public List<Twitter> Cari(@PathVariable (value = "kata") String kata){
        List<Twitter> a = null;


        try {
            a = searcher.doSearching(kata);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;
    }
}
