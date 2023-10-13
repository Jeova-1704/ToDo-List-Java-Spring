package br.com.jeovabezerraleite.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.jeovabezerraleite.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var serveletPath = request.getServletPath();
        if (serveletPath.startsWith("/tasks/")) {
            //pegar autenticacao (user, password)
            var authorization =  request.getHeader("Authorization");
            var authdecoede = authorization.substring("Basic".length()).trim();

            byte[] authoDecode  = Base64.getDecoder().decode(authdecoede);
            var authString =  new String(authoDecode);

            String[] credenciais = authString.split(":");
            String username = credenciais[0];
            String password = credenciais[1];

            //validar usuario
            var user = this.userRepository.findByUserName(username);
            if (user == null) {
                response.sendError(401);
            } else {
                // validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if(passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
                //segue viagem

                //else(erro retorna que não tem permisao )

            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
