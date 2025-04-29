package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.user.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (!"dev".equals(activeProfile)) return;

        if (userRepository.count() == 0) {
            User caio = new User("Caio", "caio@alura.com.br", Role.STUDENT);
            User paulo = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
            User lucas = new User("Lucas", "lucas@alura.com.br", Role.INSTRUCTOR);  // Novo instrutor
            User ana = new User("Ana", "ana@alura.com.br", Role.STUDENT);  // Novo estudante
            User maria = new User("Maria", "maria@alura.com.br", Role.INSTRUCTOR);
            userRepository.saveAll(Arrays.asList(caio, paulo, lucas, ana, maria));


            Course java = new Course("Java", "Aprenda Java com Alura", paulo);
            Course python = new Course("Python", "Aprenda Python com Alura", paulo);
            Course rubby = new Course("Ruby", "Aprenda Ruby com Alura", paulo);
            courseRepository.saveAll(Arrays.asList(java, python, rubby));
        }
    }
}