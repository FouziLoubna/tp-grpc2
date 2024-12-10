package ma.projet.grpc.services;



import ma.projet.grpc.entities.Compte;
import ma.projet.grpc.repositories.CompteRepository;
import ma.projet.grpc.stubs.TypeCompte;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompteService {
    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    // Obtenir tous les comptes
    public List<Compte> findAllComptes() {
        return compteRepository.findAll();
    }

    // Obtenir un compte par ID
    public Compte findCompteById(String id) {
        return compteRepository.findById(id).orElse(null);
    }

    // Sauvegarder un compte
    public Compte saveCompte(Compte compte) {
        return compteRepository.save(compte);
    }

    // Obtenir les comptes par type
    public List<Compte> findComptesByType(TypeCompte type) {
        return compteRepository.findByType(type);
    }

    // Supprimer un compte par ID
    public boolean deleteCompteById(String id) {
        if (compteRepository.existsById(id)) {
            compteRepository.deleteById(id);
            return true;
        }
        return false;
}
}