package com.example.filter;

import com.example.service.CompanyService;
import com.example.service.SeekerSignupService;
import com.example.util.JwtUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.repository.SeekerSignupRepositary;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private  SeekerSignupService seekerSignupService;
    @Autowired
    private com.example.repository.CompanyRepository companyRepository;
    @Autowired
    private  SeekerSignupRepositary seekerSignupRepositary;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //try{
            String authorizationHeader = httpServletRequest.getHeader("Authorization");

            String token = null;
            String email = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);
                email = jwtUtil.extractUsername(token);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails=null;

                if(seekerSignupRepositary.existsByEmail(email)==false)userDetails = companyService.loadUserByUsername(email);

                else userDetails= seekerSignupService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
//        }catch (Exception eje) {
//
//            ((HttpServletResponse) httpServletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            PrintWriter out = httpServletResponse.getWriter();
//            httpServletResponse.setContentType("application/json");
//            httpServletResponse.setCharacterEncoding("UTF-8");
//            Gson gson = new Gson();
//            String json = gson.toJson("Token Expired");
//            out.print(json);
//            out.flush();
//        }
    }
}