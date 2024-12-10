package ma.projet.grpc.controllers;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import ma.projet.grpc.services.CompteService;

import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {
    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }

    @Override
    public void allComptes(GetAllComptesRequest request,
                           StreamObserver<GetAllComptesResponse> responseObserver) {
        var comptes = compteService.findAllComptes().stream()
                .map(compte -> Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(GetAllComptesResponse.newBuilder()
                .addAllComptes(comptes)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request,
                           StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte();
        var compte = new ma.projet.grpc.entities.Compte();

        compte.setSolde(compteReq.getSolde());
        compte.setDateCreation(compteReq.getDateCreation());
        compte.setType(compteReq.getType().name());

        var savedCompte = compteService.saveCompte(compte);

        var grpcCompte = Compte.newBuilder()
                .setId(savedCompte.getId())
                .setSolde(savedCompte.getSolde())
                .setDateCreation(savedCompte.getDateCreation())
                .setType(TypeCompte.valueOf(savedCompte.getType()))
                .build();

        responseObserver.onNext(SaveCompteResponse.newBuilder()
                .setCompte(grpcCompte)
                .build());
        responseObserver.onCompleted();
    }
    @Override
    public void compteByType(GetComptesByTypeRequest request,
                             StreamObserver<GetComptesByTypeResponse> responseObserver) {
        var typeCompteString = request.getType();
        TypeCompte typeCompte;

        try {
            typeCompte = TypeCompte.forNumber(Integer.parseInt(String.valueOf(typeCompteString))); // Utilisez forNumber() pour obtenir l'énumération
            if (typeCompte == null) {
                throw new IllegalArgumentException("TypeCompte invalide : " + typeCompteString);
            }
        } catch (IllegalArgumentException e) {
            // Gestion de type non valide
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("TypeCompte invalide : " + typeCompteString)
                    .asRuntimeException());
            return;
        }

        var comptesByType = compteService.findComptesByType(typeCompte).stream()
                .map(compte -> Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.forNumber(Integer.parseInt(compte.getType()))) // Conversion correcte du type de compte
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(GetComptesByTypeResponse.newBuilder()
                .addAllComptes(comptesByType)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCompte(DeleteCompteRequest request,
                             StreamObserver<DeleteCompteResponse> responseObserver) {
        var compteId = request.getId();

        // Suppression du compte via le service
        boolean deleted = compteService.deleteCompteById(compteId);

        // Création d'une réponse avec un message de confirmation
        String responseMessage = deleted
                ? "Le compte avec l'ID " + compteId + " a été supprimé avec succès."
                : "Échec de la suppression : Aucun compte trouvé avec l'ID " + compteId;

        // Construction de la réponse gRPC
        DeleteCompteResponse response = DeleteCompteResponse.newBuilder()
                .setMessage(responseMessage)
                .build();

        // Envoi de la réponse
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}