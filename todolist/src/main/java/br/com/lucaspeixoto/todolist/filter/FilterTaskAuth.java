package br.com.lucaspeixoto.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.lucaspeixoto.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtém o caminho do servlet da requisição
        var servletPath = request.getServletPath();

        // Verifica se a requisição é para operações relacionadas a tarefas
        if (servletPath.startsWith("/tasks/")) {
            // Obtém o cabeçalho de autorização da requisição
            var authorization = request.getHeader("Authorization");

            // Extrai a parte codificada da string de autorização
            var authEncoded = authorization.substring("Basic".length()).trim();

            // Decodifica a string de autorização codificada
            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            // Converte a string decodificada em uma string legível
            var authString = new String(authDecode);

            // Divide a string de autorização em nome de usuário e senha
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            // Busca o usuário no repositório com base no nome de usuário fornecido
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                // Retorna um erro de não autorizado (401) se o usuário não for encontrado
                response.sendError(401);
            } else {
                // Verifica se a senha fornecida corresponde à senha armazenada no banco de dados usando BCrypt
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    // Define o atributo "idUser" na requisição com o ID do usuário autenticado
                    request.setAttribute("idUser", user.getId());
                    // Continua o encadeamento do filtro
                    filterChain.doFilter(request, response);
                } else {
                    // Retorna um erro de não autorizado (401) se a senha estiver incorreta
                    response.sendError(401);
                }
            }
        } else {
            // Se a requisição não for relacionada a tarefas, continua o encadeamento do filtro
            filterChain.doFilter(request, response);
        }

    }

}