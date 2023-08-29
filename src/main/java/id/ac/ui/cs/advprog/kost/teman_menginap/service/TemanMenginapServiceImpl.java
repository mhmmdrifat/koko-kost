package id.ac.ui.cs.advprog.kost.teman_menginap.service;

import id.ac.ui.cs.advprog.kost.core.model.JwtPayload;
import id.ac.ui.cs.advprog.kost.rent.exceptions.InvalidTenantException;
import id.ac.ui.cs.advprog.kost.teman_menginap.dto.CreateTemanMenginapRequest;
import id.ac.ui.cs.advprog.kost.teman_menginap.exceptions.InvalidEmailException;
import id.ac.ui.cs.advprog.kost.teman_menginap.exceptions.InvalidTemanMenginapTenantException;
import id.ac.ui.cs.advprog.kost.teman_menginap.exceptions.TemanMenginapDoesNotExistException;
import id.ac.ui.cs.advprog.kost.teman_menginap.model.TemanMenginap;
import id.ac.ui.cs.advprog.kost.teman_menginap.repository.TemanMenginapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class TemanMenginapServiceImpl implements TemanMenginapService {

    @Autowired
    private TemanMenginapRepository temanMenginapRepository;


    @Override
    public void delete(Integer id) {
        if (isTemanMenginapNotExist(id)) {
            throw new TemanMenginapDoesNotExistException(id);
        }
    }

    @Override
    public List<TemanMenginap> findAll() {
        return temanMenginapRepository.findAll();
    }

    @Override
    public List<TemanMenginap> findAllByTenantId(Integer userId) {
        return temanMenginapRepository.findAll().stream().filter(
                rent -> rent.getUserId().equals(userId)
        ).toList();
    }

    @Override
    public TemanMenginap findById(Integer id) {
        if (isTemanMenginapNotExist(id)) {
            throw new TemanMenginapDoesNotExistException(id);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Integer userId = ((JwtPayload) authentication.getCredentials()).getUserId();
        if (userId == null) {
            throw new InvalidTemanMenginapTenantException(userId);
        }
        TemanMenginap req = temanMenginapRepository.findById(id).orElse(null);
        assert req != null;
        if (!req.getUserId().equals(userId)) {
            throw new InvalidTemanMenginapTenantException(userId);
        }
        return temanMenginapRepository.findById(id).orElse(null);
    }


    @Override
    public TemanMenginap create(CreateTemanMenginapRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = ((JwtPayload) authentication.getCredentials()).getUserId();
        if (userId == null || !request.getUserId().equals(userId)) {
            throw new InvalidTenantException(userId);
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailPattern);
        if (!pattern.matcher(request.getEmail()).matches()) {
            throw new InvalidEmailException(request.getEmail());
        }

        TemanMenginap newTemanMenginap = TemanMenginap.builder()
                .name(request.getName())
                .email(request.getEmail())
                .reason(request.getReason())
                .userId(userId)
                .bookingStatus("Pending") // update this as per your business logic
                .build();

        return temanMenginapRepository.save(newTemanMenginap);
    }

    private boolean isTemanMenginapNotExist(Integer id) {
        return temanMenginapRepository.findById(id).isEmpty();
    }

}

