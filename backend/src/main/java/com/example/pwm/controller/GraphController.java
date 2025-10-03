package com.example.pwm.controller;
import org.springframework.graphql.data.method.annotation.QueryMapping; import org.springframework.stereotype.Controller;
@Controller public class GraphController { @QueryMapping public String hello(){ return "Hello from GraphQL"; } }
