package br.com.lucaspeixoto.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.lucaspeixoto.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    // Endpoint para criar uma nova tarefa
    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        // Obtém o ID do usuário da requisição
        var idUser = request.getAttribute("idUser");
        // Define o ID do usuário na tarefa a ser criada
        taskModel.setIdUser((UUID) idUser);

        // Verifica se a data de início e término da tarefa são válidas
        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de ínicio e término devem ser maior que a data atual");
        }

        // Verifica se a data de início é anterior à data de término
        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de ínicio deve ser menor que a data de término");
        }
        
        // Salva a tarefa no repositório
        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    // Endpoint para listar todas as tarefas do usuário
    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        // Obtém o ID do usuário da requisição
        var idUser = request.getAttribute("idUser");
        // Busca todas as tarefas associadas ao ID do usuário
        var tasks = this.taskRepository.findByIdUser((UUID)idUser);
        return tasks;
    }

    // Endpoint para atualizar uma tarefa existente
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        // Busca a tarefa pelo ID
        var task = this.taskRepository.findById(id).orElse(null);

        // Verifica se a tarefa existe
        if(task == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
        }

        // Obtém o ID do usuário da requisição
        var idUser = request.getAttribute("idUser");

        // Verifica se o usuário tem permissão para modificar a tarefa
        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem permissão para alterar está tarefa");
        }

        // Copia as propriedades não nulas da nova tarefa para a tarefa existente
        Utils.copyNonNullProperties(taskModel, task);

        // Salva a tarefa atualizada no repositório
        var taskUpdated = this.taskRepository.save(task);

        return ResponseEntity.ok().body(taskUpdated);
    }
}