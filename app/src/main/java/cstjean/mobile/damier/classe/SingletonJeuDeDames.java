package cstjean.mobile.damier.classe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Cette classe est le jeu de dames qui calcule les mouvements possibles.
 */
public class SingletonJeuDeDames {
    /**
     * Instance du singleton du jeu de dames.
     */
    private static SingletonJeuDeDames instance = null;
    /**
     * Cette variable est le damier.
     */
    private static Damier damier;
    /**
     * Cette variable est la liste des pions transformés en dames.
     */
    private final List<Integer> transformationsEnDames = new ArrayList<>();
    /**
     * Cette variable est l'historique de déplacement selon la notation manoury.
     */
    private final List<String> historiqueDeplacementDamier = new ArrayList<>();
    /**
     * Cette variable est la liste de pions pris.
     */
    private final ArrayList<Object[]> listePionsPris = new ArrayList<>();
    /**
     * Cette variable représente les contours du board gauche et droite.
     */
    private final List<Integer> borderBoardLeftRight =
            Arrays.asList(1, 5, 6, 16, 26, 36, 46, 50, 45, 35, 25, 15);
    /**
     * Cette variable représente les contours du board haut et bas.
     */
    private final List<Integer> borderBoardtopBottom =
            Arrays.asList(1, 2, 3, 4, 5, 46, 47, 48, 49, 50);
    /**
     * Cette variable boolean gere le tour du joueur.
     */
    private boolean estTourBlanc = true;
    /**
     * Cette variable est pour le changement d'orientation dans l'application.
     */
    private static boolean estPartieCommence = false;

    /**
     * Le constructeur du jeu de dames instantie un damier.
     *
     * @return L'instance du SingletonJeuDeDames.
     */
    public static SingletonJeuDeDames getInstance() {

        if (instance == null) {
            damier = new Damier();
            instance = new SingletonJeuDeDames();
        }
        return instance;
    }

    /**
     * Getter pour la liste.
     *
     * @return La variable liste de pions de pris.
     */
    public ArrayList<Object[]> getListePionsPris() {
        return listePionsPris;
    }

    /**
     * Getter pour le tour du joueur blanc.
     *
     * @return estTourBlanc.
     */
    public boolean getEstTourBlanc() {
        return this.estTourBlanc;
    }

    /**
     * Getter pour le damier.
     *
     * @return Damier.
     */
    public Damier getDamier() {
        return damier;
    }

    /**
     * Cette methode gere les mouvements des pions et dames pour validation.
     *
     * @param positionDepart  Position de depart du pion ou dame.
     * @param positionArrivee Position d'arrivée du pion ou dame.
     */
    public void bouger(int positionDepart, int positionArrivee) {
        if (!getEstPartieCommence()) {
            estPartieCommence = true;
        }
        Pion pion = damier.getPion(positionDepart);
        if (pion == null) {
            throw new IllegalArgumentException("Mouvement impossible, aucun pion.");
        }
        char representationPion = pion.getRepresentation();
        Pion.Couleur couleurPion = pion.getCouleurPion();

        // Vérifie si le mouvement est une diagonale.
        boolean estDiagonale = Math.abs(positionArrivee - positionDepart) % 6 == 0 ||
                Math.abs(positionArrivee - positionDepart) % 5 == 0 ||
                Math.abs(positionArrivee - positionDepart) % 4 == 0;

        // Vérifie si la couleur du pion correspond avec le tour des joueurs selon la couleur
        // et le tour du joueur ainsi que la representation pour les dames.
        boolean estMouvementAdmissible =
                (couleurPion == Pion.Couleur.Noir && !estTourBlanc &&
                        positionDepart < positionArrivee) ||
                        (couleurPion == Pion.Couleur.Blanc && estTourBlanc &&
                                positionDepart > positionArrivee) ||
                        (representationPion == 'D' && !estTourBlanc) ||
                        (representationPion == 'd' && estTourBlanc);

        if (representationPion == 'P' || representationPion == 'p') {
            if (estMouvementAdmissible) {
                gererMouvementPion(positionDepart, positionArrivee, pion, estDiagonale);
            } else {
                throw new IllegalArgumentException("Mouvement impossible");
            }
        } else if (representationPion == 'D' || representationPion == 'd') {
            gererMouvementDame(positionDepart, positionArrivee, pion);
        }
    }

    /**
     * Cette methode gere le mouvement pion.
     *
     * @param positionDepart  Position de depart du pion.
     * @param positionArrivee Position d'arrivée du pion.
     * @param pion            Pion a bouger
     * @param estDiagonale    si le mouvement est en Diagonale.
     */
    private void gererMouvementPion(int positionDepart, int positionArrivee, Pion pion,
                                    boolean estDiagonale) {

        // Vérifie si le mouvement est valide.
        if (estDiagonale) {
            if (damier.getListPion().get(positionArrivee) == null) {
                if (estTourBlanc && positionArrivee >= 1 && positionArrivee <= 5) {
                    pion = new Dames(Pion.Couleur.Blanc);
                    damier.ajouterDames(positionArrivee, pion, positionDepart);
                    transformationsEnDames.add(positionArrivee);
                } else if (!estTourBlanc && positionArrivee >= 46 && positionArrivee <= 50) {
                    pion = new Dames(Pion.Couleur.Noir);
                    damier.ajouterDames(positionArrivee, pion, positionDepart);
                    transformationsEnDames.add(positionArrivee);
                }
                manouryHistoryBuilder(positionDepart, positionArrivee);
                damier.ajouterPion(positionArrivee, pion);
                damier.retirerPion(positionDepart);
                estTourBlanc = !estTourBlanc;
            } else {
                int finalPos = getFinalPositionPrise(positionDepart, positionArrivee);
                Pion pionFinish = damier.getPion(positionArrivee + finalPos);
                Pion pionArrivee = damier.getPion(positionArrivee);
                if (pionFinish == null && pionArrivee != null &&
                        pionArrivee.getCouleurPion() != pion.getCouleurPion()) {
                    List<Integer> mvt = mouvementsPossibles(positionDepart, true)
                            .stream().distinct().collect(Collectors.toList());
                    handleCaptureMouvementPossible(positionDepart, positionArrivee, mvt);
                    manouryHistoryBuilder(positionDepart, finalPos);
                }
            }
        } else {
            throw new IllegalArgumentException("Mouvement impossible, " +
                    "mouvement diagonale seulement.");
        }
    }

    /**
     * Cette methode gere le mouvement des dames.
     *
     * @param positionDepart  Position depart de la dame.
     * @param positionArrivee Position arrive de la dame.
     * @param pion            la Dame.
     */
    private void gererMouvementDame(int positionDepart, int positionArrivee,
                                    Pion pion) {
        List<Integer> mvtDepart = mouvementsPossibles(positionDepart, true);
        Pion pionEnnemi = damier.getPion(positionArrivee);
        if (mvtDepart.contains(positionArrivee)) {
            if (pionEnnemi == null) {
                manouryHistoryBuilder(positionDepart, positionArrivee);
                damier.ajouterPion(positionArrivee, pion);
                damier.retirerPion(positionDepart);
                estTourBlanc = !estTourBlanc;
            }
        } else {
            throw new IllegalArgumentException("Mouvement impossible");
        }
    }

    /**
     * Cette methode gère la prise d'un pion ennemi par une dame.
     *
     * @param positionDepart Position de la dame
     * @param positionEnnemi Position de l'ennemi
     * @param positionFinal  Position final apres la prise.
     */
    private void priseDame(int positionDepart, int positionEnnemi, int positionFinal, Pion pion) {
        listePionsPris.add(new Object[]{damier.getListPion().get(positionEnnemi), positionEnnemi});
        damier.retirerPion(positionDepart);
        damier.retirerPion(positionEnnemi);
        damier.ajouterPion(positionFinal, pion);
        manouryHistoryBuilderPrise(positionDepart, positionFinal);
        estTourBlanc = !estTourBlanc;
    }

    /**
     * Cette methode creer le string nécessaire pour l'historique en manoury.
     *
     * @param positionDepart  Position de départ du pion.
     * @param positionArrivee Position de départ du pion.
     */
    private void manouryHistoryBuilder(int positionDepart, int positionArrivee) {
        StringBuilder sb = new StringBuilder();
        sb.append(positionDepart)
                .append("-")
                .append(positionArrivee);

        if (!estTourBlanc) {
            sb.append(")").insert(0, "(");
        }
        historiqueDeplacementDamier.add(sb.toString());
    }

    /**
     * Cette methode creer le string nécessaire pour l'historique en manoury.
     *
     * @param positionDepart  Position de départ du pion.
     * @param positionArrivee Position de départ du pion.
     */
    private void manouryHistoryBuilderPrise(int positionDepart, int positionArrivee) {
        StringBuilder sb = new StringBuilder();
        sb.append(positionDepart)
                .append("x")
                .append(positionArrivee);

        if (!estTourBlanc) {
            sb.append(")").insert(0, "(");
        }
        historiqueDeplacementDamier.add(sb.toString());
    }

    /**
     * Cette methode gere la prise de pion.
     *
     * @param positionDepart  Position de depart.
     * @param enemyPosition   Position de l'ennemi.
     * @param positionArrivee Position d'arrivée.
     */
    public void prisePion(int positionDepart, int enemyPosition, int positionArrivee) {
        Pion pion = damier.getListPion().get(positionDepart);
        Pion pionEnemy = damier.getListPion().get(enemyPosition);

        if (pion == null || pionEnemy == null) {
            throw new IllegalArgumentException("Prise impossible, pion(s) null(s).");
        }

        if (pion.getCouleurPion() == pionEnemy.getCouleurPion()) {
            throw new IllegalArgumentException("Prise impossible, même couleur.");
        }
        if (borderBoardLeftRight.contains(enemyPosition)) {
            return;
        }
        if (borderBoardtopBottom.contains(enemyPosition)) {
            return;
        }
        if (verifierPriseValide(positionDepart, enemyPosition, positionArrivee)) {
            listePionsPris.add(new Object[]{damier.getListPion().get(enemyPosition), enemyPosition});

            manouryHistoryBuilderPrise(positionDepart, positionArrivee);

            switch (pion.getCouleurPion()) {
                case Blanc -> {
                    if (positionArrivee <= 5) {
                        damier.ajouterDames(positionArrivee, pion, positionDepart);
                        damier.retirerPion(enemyPosition);
                        estTourBlanc = !estTourBlanc;
                        return;
                    }
                }
                case Noir -> {
                    if (positionArrivee > 45) {
                        damier.ajouterDames(positionArrivee, pion, positionDepart);
                        damier.retirerPion(enemyPosition);
                        estTourBlanc = !estTourBlanc;
                        return;
                    }
                }
                default -> {
                    return;
                }
            }
            damier.ajouterPion(positionArrivee, pion);
            damier.retirerPion(enemyPosition);
            damier.retirerPion(positionDepart);
            estTourBlanc = !estTourBlanc;
        } else {
            throw new IllegalArgumentException("Prise impossible.");
        }
    }

    /**
     * Vérifie si une prise est valide.
     *
     * @param positionDepart  prend en paramètre la position de départ.
     * @param enemyPosition   prend en paramètre la position de l'ennemi.
     * @param positionArrivee prend en paramètre la position d'arrivée.
     * @return retourne true si la prise est valide.
     */
    private boolean verifierPriseValide(int positionDepart, int enemyPosition, int positionArrivee) {
        return Math.abs(enemyPosition - positionDepart) <= 6 &&
                Math.abs(enemyPosition - positionDepart) >= 4 &&
                Math.abs(positionArrivee - enemyPosition) <= 6 &&
                Math.abs(positionArrivee - enemyPosition) >= 4;
    }

    /**
     * Cette méthode retourne un mouvement de l'historique selon l'index donné.
     *
     * @param i prend un paramètre un index i.
     * @return retourne un string de mouvement ou de prise.
     */
    public String getHistoriqueDeplacementDamierPosition(int i) {
        return historiqueDeplacementDamier.get(i);
    }

    /**
     * Cette méthode retourne l'historique complet des mouvements et prises.
     *
     * @return retourne une liste de string contenant les mouvements et les prises.
     */
    public List<String> getHistoriqueDeplacementDamier() {
        return historiqueDeplacementDamier;
    }

    /**
     * Getter pour la partie commence.
     *
     * @return boolean qui retourne true si la partie est commence.
     */
    public boolean getEstPartieCommence() {
        return estPartieCommence;
    }

    /**
     * Cette methode est pour retourner en arriere d'un mouvement.
     */
    public void retourPartie() {
        int index = historiqueDeplacementDamier.size() - 1;
        String dernierMouvement = historiqueDeplacementDamier.get(index);

        boolean estPrise = false;
        int positionArrivee = -1;
        int positionDepart = -1;

        // Regex qui sépare les nombres
        String regex = "(\\d+)([-x])(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dernierMouvement);

        // Fonction qui identifie si c'est une prise ou un mouvement
        if (matcher.find()) {
            positionDepart = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
            String separator = matcher.group(2);

            if (Objects.equals(separator, "x")) {
                estPrise = true;
            }
            positionArrivee = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
        }

        // Determiner si le tour était au blanc ou noir et rétablie l'ancien tour
        determinerTour(dernierMouvement);

        Pion pionAbouger = damier.getPion(positionArrivee);
        if (!estPrise) {
            retourMouvement(pionAbouger, positionArrivee, positionDepart);
        } else {
            retourPrise(positionArrivee, positionDepart);
        }

        historiqueDeplacementDamier.remove(index);
    }

    public List<Integer> getTransformationsEnDames() {
        return transformationsEnDames;
    }

    /**
     * Fait le retour en arrière d'un mouvement.
     *
     * @param pion            Prend en paramètre le pion bougé.
     * @param positionArrivee Prend en paramètre la posision d'arrivée.
     * @param positionDepart  Prend en paramètre la position de départ.
     */
    private void retourMouvement(Pion pion, int positionArrivee, int positionDepart) {
        boolean isDame = (pion.getRepresentation() == 'd' || pion.getRepresentation() == 'D');
        boolean isTransformationEnDame = getTransformationsEnDames().contains(positionArrivee);

        Pion.Couleur couleurPion = estTourBlanc ? Pion.Couleur.Blanc : Pion.Couleur.Noir;
        if (isDame) {
            if (isTransformationEnDame) {
                pion = isTransformationEnDame ? new Pion(couleurPion) : new Dames(couleurPion);
                transformationsEnDames.remove(transformationsEnDames.size() - 1);
            } else {
                pion = new Dames(couleurPion);
            }
        } else {
            pion = new Pion(couleurPion);
        }

        damier.ajouterPion(positionDepart, pion);
        damier.retirerPion(positionArrivee);
    }

    /**
     * Fait le retour en arrière d'une prise.
     *
     * @param positionArrivee Prend en paramètre la posision d'arrivée.
     * @param positionDepart  Prend en paramètre la position de départ.
     */
    private void retourPrise(int positionArrivee, int positionDepart) {
        int indexEnemy = listePionsPris.size() - 1;

        Pion pionDeplace = damier.getPion(positionArrivee);
        Pion pionEnemy = (Pion) listePionsPris.get(indexEnemy)[0];
        int positionEnemy = (int) listePionsPris.get(indexEnemy)[1];

        listePionsPris.remove(indexEnemy);

        damier.ajouterPion(positionDepart, pionDeplace);
        damier.ajouterPion(positionEnemy, pionEnemy);
        damier.retirerPion(positionArrivee);
    }

    /**
     * Fontionne qui attribue le bon tour lors d'un retour en arrière.
     *
     * @param dernierMouvement Prend un paramètre un string du dernier mouvement en manoury.
     */
    private void determinerTour(String dernierMouvement) {
        if (dernierMouvement.contains("(")) {
            dernierMouvement = dernierMouvement.replace("(", "").replace(")", "");
            estTourBlanc = false;
        } else {
            estTourBlanc = true;
        }
    }

    /**
     * Récupère une liste de mouvements possibles pour le pion ou la dame à la position spécifiée.
     *
     * @param position           La position du pion ou de la dame.
     * @param checkPartieTermine Le boolean est la pour eviter les prises automatiques durant le
     *                           roulement de estPartieTerminee()
     * @return Une liste de mouvements possibles.
     */
    public List<Integer> mouvementsPossibles(int position, boolean checkPartieTermine) {
        List<Integer> mouvements = new ArrayList<>();
        Pion pion = damier.getListPion().get(position);
        if (pion == null) {
            return mouvements;
        }
        Direction[] decalages = getDirection(pion, position);

        for (Direction decalage : decalages) {
            evaluateDecalage(position, decalage, mouvements, checkPartieTermine);
        }
        mouvements = mouvements.stream().distinct().collect(Collectors.toList());

        return mouvements;
    }

    /**
     * Renvoie les décalages possibles pour un pion donné à une position spécifiée.
     *
     * @param pion     Le pion pour lequel on veut déterminer les décalages.
     * @param position La position actuelle du pion sur le damier.
     * @return Un tableau des décalages possibles pour le pion à la position donnée.
     */
    private Direction[] getDirection(Pion pion, int position) {
        boolean isIndexZero = position % 10 >= 1 && position % 10 <= 5;
        char representation = pion.getRepresentation();

        if (representation == 'P') {
            return isIndexZero ? Direction.getDirectionBasStartBlanc() :
                    Direction.getDirectionBasStartNoir();
        }
        if (representation == 'p') {
            return isIndexZero ? Direction.getDirectionHautStartBlanc() :
                    Direction.getDirectionHautStartNoir();
        }
        if (representation == 'd' || representation == 'D') {
            if (borderBoardLeftRight.contains(position)) {
                if (Arrays.asList(6, 16, 26, 36).contains(position)) {
                    return Direction.getDirectionDameStartNoirVerDroite();
                }
                if (Arrays.asList(15, 25, 35, 45).contains(position)) {
                    return Direction.getDirectionDameStartBlancVersDroite();
                }
                if (position == 5) {
                    return Direction.getDirectionStartBlancFifthCase();
                }
                if (position == 50) {
                    return Direction.getDirectionHautStartNoir();
                }
            }
            if (isIndexZero) {
                return Direction.getDirectionStartBlanc();
            }
        }
        return Direction.getDirectionStartNoir();
    }

    /**
     * Évalue le décalage à partir d'une position donnée et ajoute les mouvements possibles.
     *
     * @param position   La position actuelle sur le damier.
     * @param decalage   Le décalage a évalué.
     * @param mouvements Une liste pour stocker les mouvements possibles.
     */
    private void evaluateDecalage(int position, Direction
            decalage, List<Integer> mouvements,
                                  boolean checkPartieTermine) {
        int nouvellePosition = position + decalage.getValue();

        if (!estPositionValide(nouvellePosition)) {
            return;
        }

        Pion currentPion = damier.getListPion().get(position);
        Pion targetPion = damier.getListPion().get(nouvellePosition);
        if (targetPion == null) {
            addPossibleMovements(position, nouvellePosition, decalage, mouvements, checkPartieTermine);
        } else {
            if (currentPion != null) {
                if (targetPion.getCouleurPion() != currentPion.getCouleurPion()) {
                    int positionFinal = getFinalPositionPrise(position, nouvellePosition);
                    if (damier.getPion(nouvellePosition + positionFinal) == null) {
                        if (!checkPartieTermine) {
                            prisePion(position, nouvellePosition, nouvellePosition + positionFinal);
                        }
                    }
                }
            }
        }
    }

    /**
     * Ajoute les mouvements possibles à une liste pour une position et un décalage donnés.
     *
     * @param nouvellePosition La nouvelle position à évaluer.
     * @param directionIndex   Enum de la direction.
     * @param mouvements       La liste dans laquelle ajouter les mouvements possibles.
     */
    private void addPossibleMovements(int position, int nouvellePosition, Direction directionIndex,
                                      List<Integer> mouvements, boolean checkPartieTermine) {
        Pion pion = damier.getPion(position);
        int positionInitiale = nouvellePosition - directionIndex.getValue();

        // Ce boolean est pour une alternance -1 ou + 1 dans le mouvement de diagonale de la dame.
        boolean alternance = false;

        if (pion != null && !estPionOuDame(position)) {
            mouvements.add(nouvellePosition);

            // Utilise gestion AlternanceDame pour la logique de déplacement de la dame
            gestionAlternance(nouvellePosition, directionIndex, mouvements, alternance, checkPartieTermine);
        }

        if (pion != null && estPionOuDame(position)) {
            mouvements.add(nouvellePosition);
            if (!checkBoardPion(positionInitiale, pion, mouvements)) {
                gestionAlternance(nouvellePosition, directionIndex, mouvements, alternance, checkPartieTermine);
            }
        }
    }

    /**
     * Cette methode fait l'alternance pour ajouter aux mouvements possible.
     * Si le pion est une dame il entre dans une boucle pour placer chaque mouvement possible
     * sinon on retourne la position dans la liste.
     *
     * @param nouvellePosition Nouvelle position à vérifier
     * @param directionIndex   Enum de direction à faire l'alternance
     * @param mouvements       Liste de mouvements du pionInitiale
     * @param alternance       Boolean pour faire l'alternance + 1 ou -1.
     */
    private void gestionAlternance(int nouvellePosition, Direction directionIndex,
                                   List<Integer> mouvements, boolean alternance,
                                   boolean checkPartieTermine) {
        int positionInitiale = nouvellePosition - directionIndex.getValue();
        int finalPosition;
        Pion pionInitiale = damier.getPion(positionInitiale);
        String directionStart = directionIndex.toString();
        int directionMove = directionIndex.getValue();
        if (borderBoardLeftRight.contains(nouvellePosition) ||
                borderBoardtopBottom.contains(nouvellePosition) && !checkPartieTermine) {
            mouvements.add(nouvellePosition);
            return;
        }

        // Ajoute la position final si c'est un pion ou une dame
        if (pionInitiale != null && estPionOuDame(positionInitiale)) {
            mouvements.add(nouvellePosition);
            return;
        }

        // Boucle qui s'occupe des mouvements de la dame
        while (estPositionValide(nouvellePosition)) {
            Pion pionPossibleNouvellePosition = damier.getPion(nouvellePosition);
            if (pionPossibleNouvellePosition != null) {
                break;
            }
            if (!borderBoardtopBottom.contains(nouvellePosition) || !borderBoardLeftRight.contains(nouvellePosition)) {
                if (directionStart.contains("START_NOIR")) {
                    nouvellePosition += alternance ? directionMove : directionMove + 1;
                    finalPosition = nouvellePosition + (!alternance ? directionMove : directionMove + 1);
                } else {
                    nouvellePosition += alternance ? directionMove : directionMove - 1;
                    finalPosition = nouvellePosition + (!alternance ? directionMove : directionMove - 1);
                }
                if (borderBoardLeftRight.contains(nouvellePosition) ||
                        borderBoardtopBottom.contains(nouvellePosition)) {
                    if (damier.getPion(nouvellePosition) == null) {
                        mouvements.add(nouvellePosition);
                    }
                    return;
                }
                if (damier.getPion(nouvellePosition) == null) {
                    alternance = !alternance;
                    mouvements.add(nouvellePosition);
                }
                Pion pionPossibleEnnemi = damier.getPion(nouvellePosition);
                if (pionPossibleEnnemi != null && pionInitiale != null && !checkPartieTermine) {
                    if (pionPossibleEnnemi.getCouleurPion() != pionInitiale.getCouleurPion()) {
                        priseDame(positionInitiale, nouvellePosition, finalPosition, pionInitiale);
                    }
                    break;
                }

            } else {
                break;
            }
        }

    }

    /**
     * Cette methode verifie si un pion est positionner sur les côtés du damier et retourner
     * les mouvements possibles.
     *
     * @param positionInitial Position initial du pion.
     * @param pion            Pion
     * @param mouvements      Liste de mouvements du pion
     * @return Boolean qui confirme le checkPartieTerminee
     */
    public boolean checkBoardPion(int positionInitial, Pion pion, List<Integer> mouvements) {
        if (pion == null) {
            return false;
        }
        boolean estPionBlanc = pion.getCouleurPion() ==
                Pion.Couleur.Blanc && pion.getRepresentation() == 'p';
        boolean estPionNoir = pion.getCouleurPion() ==
                Pion.Couleur.Noir && pion.getRepresentation() == 'P';

        if (borderBoardLeftRight.contains(positionInitial)) {
            if (estPionBlanc) {
                return handlePionBlancMovesBorder(positionInitial, mouvements);
            } else if (estPionNoir) {
                return handlePionNoirMovesBorder(positionInitial, mouvements);
            }
        }
        return false;
    }

    /**
     * Cette methode est liée au checkBoardPion pour retourner l'emplacement sur lequel
     * le pion blanc doit aller.
     *
     * @param position Position du pion
     * @param moves    Liste de mouvements
     * @return Retourne un boolean pour les checkPartieTerminee
     */
    public boolean handlePionBlancMovesBorder(int position, List<Integer> moves) {
        moves.clear();
        if (damier.getPion(position) == null) {
            return false;
        }

        switch (position) {
            case 6, 16, 26, 36, 46, 15, 25, 35, 45 -> moves.add(position - 5);
            default -> {
                moves.add(44);
                moves.add(45);
            }
        }
        return true;
    }

    /**
     * Cette methode est liée au checkBoardPion pour retourner l'emplacement sur lequel le pion noir
     * doit aller.
     *
     * @param position Position du pion
     * @param moves    Liste de mouvements
     * @return Retourne un boolean pour les checkPartieTerminee
     */
    public boolean handlePionNoirMovesBorder(int position, List<Integer> moves) {
        moves.clear();
        if (damier.getPion(position) == null) {
            return false;
        }

        switch (position) {
            case 1 -> {
                moves.add(7);
                moves.add(6);
            }
            case 6, 16, 26, 36, 5, 15, 25, 35 -> moves.add(position + 5);
            default -> moves.add(50);
        }
        return true;
    }

    /**
     * Cette methode verifie si le pion est une dame ou un pion.
     *
     * @param position position à vérifier.
     * @return boolean True si est un pion, false pour une dames
     */
    private boolean estPionOuDame(int position) {
        Pion pion = damier.getPion(position);
        if (pion == null) {
            return false;
        }
        char representation = pion.getRepresentation();
        return representation == 'P' || representation == 'p';
    }

    /**
     * Traite un mouvement possible de capture.
     *
     * @param position         La position actuelle du pion.
     * @param nouvellePosition La position après le mouvement.
     * @param mouvements       Une liste pour stocker les mouvements possibles.
     */
    private void handleCaptureMouvementPossible(int position, int nouvellePosition, List<
            Integer> mouvements) {
        int index = getFinalPositionPrise(position, nouvellePosition);

        mouvements.add(nouvellePosition);
        prisePion(position, nouvellePosition, nouvellePosition + index);
    }

    /**
     * Renvoie la position finale après une prise.
     *
     * @param position         La position actuelle du pion.
     * @param nouvellePosition La position après le mouvement de prise.
     * @return La position finale après la prise.
     */
    public int getFinalPositionPrise(int position, int nouvellePosition) {
        int[] direction = (nouvellePosition % 10 >= 1 && nouvellePosition % 10 <= 5) ?
                new int[]{6, 5, -5, -4} : new int[]{5, 4, -6, -5};
        int[] mapping = {direction[2], direction[3], direction[0], direction[1]};

        for (int i = 0; i < direction.length; i++) {
            if (nouvellePosition + direction[i] == position) {
                return mapping[i];
            }
        }
        return 0;
    }

    /**
     * Vérifie si la partie est terminée.
     *
     * @return Vrai si la partie est terminée, faux sinon on continue.
     */
    public boolean estPartieTerminee() {
        TreeMap<Integer, Pion> map = getPionRestants();
        int[] compteur = compteurPionBlancNoir();
        int cptBlanc = compteur[0];
        int cptNoir = compteur[1];
        int cptMvtRestant = 0;
        if (cptNoir != 0 && cptBlanc != 0) {
            for (Map.Entry<Integer, Pion> i : map.entrySet()) {
                int position = i.getKey();
                if (i.getValue() != null) {
                    if (!estMouvementRestant(position, true)) {
                        cptMvtRestant++;
                    }
                }
            }
            return cptMvtRestant == 0;
        }
        return true;
    }

    /**
     * Vérifie s'il y a des mouvements possible pour la position.
     *
     * @param position position a verifier.
     * @return boolean True si mouvement restants, false si aucun mouvement.
     */
    private boolean estMouvementRestant(int position, boolean checkPartieTerminee) {
        return mouvementsPossibles(position, checkPartieTerminee).isEmpty();
    }

    /**
     * Compteur de pion blanc et noir pour la fonction partieTerminee.
     *
     * @return Array des pions.
     */
    private int[] compteurPionBlancNoir() {
        int cptBlanc = 0;
        int cptNoir = 0;
        TreeMap<Integer, Pion> map = getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Pion pion = i.getValue();
            if (pion.getCouleurPion() == Pion.Couleur.Blanc) {
                cptBlanc++;
            } else {
                cptNoir++;
            }
        }
        return new int[]{cptBlanc, cptNoir};
    }

    /**
     * Cette methode compte le nombre de pions restants dans sur le damier.
     *
     * @return Un treeMap avec les emplacements des pions restants.
     */
    public TreeMap<Integer, Pion> getPionRestants() {
        TreeMap<Integer, Pion> map = new TreeMap<>();
        for (int i = 1; i <= 51; i++) {
            if (damier.getListPion().get(i) != null) {
                map.put(i, damier.getListPion().get(i));
            }
        }
        return map;
    }

    /**
     * Vérifie si une position est dans les limites valides.
     *
     * @param position La position à verifier.
     * @return Vrai si valide, faux sinon.
     */
    public boolean estPositionValide(int position) {
        return position > 0 && position <= 50;
    }

    /**
     * Cette methode reinitialise le jeu de dames.
     */
    public void reset() {
        estTourBlanc = true;
        historiqueDeplacementDamier.clear();
        listePionsPris.clear();
        damier.initialiser();
    }

    /**
     * Cette enum est pour la direction. À noter que la couleur à la suite du START signifie la couleur de la première
     * case à partir de la gauche.
     */
    public enum Direction {
        /**
         * Cette enum signifie la rangée avec un case blanche au départ vers le bas et vers la droite avec
         * une valeur de 6.
         */
        BAS_DROIT_START_BLANC(6),
        /**
         * Cette enum signifie la rangée avec un case blanche au départ vers le bas et vers la gauche avec
         * une valeur de 5.
         */
        BAS_GAUCHE_START_BLANC(5),
        /**
         * Cette enum signifie la rangée avec un case blanche au départ vers le haut et vers la gauche avec
         * une valeur de -5.
         */
        HAUT_GAUCHE_START_BLANC(-5),
        /**
         * Cette enum signifie la rangée avec un case blanche au départ vers le haut et vers la droite avec
         * une valeur de -4.
         */
        HAUT_DROIT_START_BLANC(-4),
        /**
         * Cette enum signifie la rangée avec un case noir au départ vers le bas et vers la droite avec
         * une valeur de 5.
         */
        BAS_DROIT_START_NOIR(5),
        /**
         * Cette enum signifie la rangée avec un case noir au départ vers le bas et vers la gauche avec
         * une valeur de 4.
         */
        BAS_GAUCHE_START_NOIR(4),
        /**
         * Cette enum signifie la rangée avec un case noir au départ vers le haut et vers la droite avec
         * une valeur de -5.
         */
        HAUT_DROIT_START_NOIR(-5),
        /**
         * Cette enum signifie la rangée avec un case noir au départ vers le haut et vers la gauche avec
         * une valeur de -6.
         */
        HAUT_GAUCHE_START_NOIR(-6);
        /**
         * Cette variable est la valeur associée à l'enum.
         */
        private final int value;

        /**
         * Setter de la valeur à l'enum.
         *
         * @param value valeur de l'enum.
         */
        Direction(int value) {
            this.value = value;
        }

        /**
         * Cette méthode statique retourne un array de direction pour une dame avec une rangée dont la première case
         * à gauche est blanc.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionStartBlanc() {
            return new Direction[]{
                BAS_DROIT_START_BLANC,
                BAS_GAUCHE_START_BLANC,
                HAUT_GAUCHE_START_BLANC,
                HAUT_DROIT_START_BLANC
            };
        }

        /**
         * Cette méthode statique retourne un array de direction pour une dame avec une rangée dont la première case
         * à gauche est Noir.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionStartNoir() {
            return new Direction[]{
                BAS_DROIT_START_NOIR,
                BAS_GAUCHE_START_NOIR,
                HAUT_GAUCHE_START_NOIR,
                HAUT_DROIT_START_NOIR
            };
        }

        /**
         * Cette méthode retourne un array de direction d'un pion vers le bas sur une rangée dont la première case est
         * blanc.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionBasStartBlanc() {
            return new Direction[]{
                BAS_DROIT_START_BLANC,
                BAS_GAUCHE_START_BLANC,
            };
        }

        /**
         * Cette méthode retourne un array de direction d'un pion vers le bas sur une rangée dont la première case est
         * noir.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionBasStartNoir() {
            return new Direction[]{
                BAS_DROIT_START_NOIR,
                BAS_GAUCHE_START_NOIR,
            };
        }

        /**
         * Cette méthode retourne un array de direction d'un pion vers le haut sur une rangée dont la première case est
         * blanc.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionHautStartBlanc() {
            return new Direction[]{
                HAUT_GAUCHE_START_BLANC,
                HAUT_DROIT_START_BLANC
            };
        }

        /**
         * Cette méthode retourne un array de direction d'un pion vers le haut sur une rangée dont la première case est
         * noir.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionHautStartNoir() {
            return new Direction[]{
                HAUT_GAUCHE_START_NOIR,
                HAUT_DROIT_START_NOIR
            };
        }

        /**
         * Cette méthode retourne un array de direction d'une dame qui est sur le cote gauche
         * vers le haut sur une rangée dont la première case est noir.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionDameStartNoirVerDroite() {
            return new Direction[]{
                BAS_DROIT_START_NOIR,
                HAUT_DROIT_START_NOIR
            };
        }

        /**
         * Cette méthode retourne un array de direction d'une dame qui est sur le cote droite
         * vers le haut sur une rangée dont la première case est noir.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionDameStartBlancVersDroite() {
            return new Direction[]{
                BAS_GAUCHE_START_BLANC,
                HAUT_GAUCHE_START_BLANC
            };
        }

        /**
         * Cette méthode retourne un array de direction d'une dame lorsqu'elle sur la case 5.
         *
         * @return Array de Direction
         */
        public static Direction[] getDirectionStartBlancFifthCase() {
            return new Direction[]{
                BAS_GAUCHE_START_BLANC
            };
        }

        /**
         * Cette méthode statique renvoi toutes les valeurs d'un array de direction
         * dans une List d'entier.
         *
         * @param directions Array de directions à trouver leurs valeurs.
         * @return Retourne une List
         */
        public static List<Integer> getAllValue(Direction[] directions) {
            List<Integer> result = new ArrayList<>();
            for (Direction direction : directions) {
                result.add(direction.getValue());
            }
            return result;
        }

        /**
         * Getter de la valeur d'un enum.
         *
         * @return La valeur de l'enum
         */
        public int getValue() {
            return value;
        }

    }

    /**
     * Cette fonction appelle la fonction vider du damier pour retirer tous les pions du damier.
     */
    public void vider() {
        damier.vider();
    }
}