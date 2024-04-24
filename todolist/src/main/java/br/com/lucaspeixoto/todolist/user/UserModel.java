package br.com.lucaspeixoto.todolist.user;

import lombok.Data;

@Data //lombok cria automaticamente os getters and setters usando o @Data. Caso necessário ter apenas o getter ou setter em um atributo basta fazer a anotação em cima do atributo
public class UserModel {
    private String username;
    private String name;
    private String password;


}
