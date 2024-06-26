package br.com.lucaspeixoto.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;

    @Column(length = 50)
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String priority;
    private UUID idUser;

    // Anotação para indicar que o campo é preenchido automaticamente com a data e hora de criação
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Método setter personalizado para o título da tarefa
    public void setTitle(String title) throws Exception{
        // Verifica se o título excede o comprimento máximo permitido
        if(title.length() > 50){
            throw new Exception("O campo title deve conter no máximo 50 caracteres!");
        }
        this.title = title;
    }

}