package com.example.springai.controller;


import com.alibaba.cloud.ai.graph.CompiledGraph;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/graph")
public class GraphController {

    private final CompiledGraph compiledGraph;

    public GraphController(CompiledGraph compiledGraph){
        this.compiledGraph = compiledGraph;
    }

}
