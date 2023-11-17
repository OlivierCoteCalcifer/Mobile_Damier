package cstjean.mobile.damier.classe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Cette variable est l'historique de déplacement selon la notation manoury.
     */
    private final List<String> historiqueDeplacementDamier = new ArrayList<>();
    /**
     * Cette variable est la liste de pions pris.
     */
    private final ArrayList<Object[]> listePionsPris = new ArrayList<>();
    /**
     * Cette variable représente les contours du board.
     */
    private final List<Integer> borderBoard =
            Arrays.asList(1, 2, 3, 4, 5, 6, 16, 26, 36, 46, 47, 48, 49, 50, 45, 35, 25, 15);
    /**
     * Cette variable boolean gere le tour du joueur.
     */
    private boolean estTourBlanc = true;

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
        Pion pion = damier.getListPion().get(positionDepart);
        char representationPion = pion.getRepresentation();
        Pion.Couleur couleurPion = pion.getCouleurPion();

        if (pion == null) {
            throw new IllegalArgumentException("Mouvement impossible, aucun pion.");
        }

        // Vérifie si le mouvement est une diagonale.
        boolean estDiagonale = Math.abs(positionArrivee - positionDepart) % 6 == 0 ||
                Math.abs(positionArrivee - positionDepart) % 5 == 0 ||
                Math.abs(positionArrivee - positionDepart) % 4 == 0;

        // Vérifie si la couleur du pion correspond avec le tour des joueurs.
        boolean estMouvementAdmissible =
                (couleurPion == Pion.Couleur.Noir && !estTourBlanc && positionDepart < positionArrivee) ||
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
            gererMouvementDame(positionDepart, positionArrivee, estDiagonale, pion);
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
    private void gererMouvementPion(int positionDepart, int positionArrivee, Pion pion, boolean estDiagonale) {

        // Vérifie si le mouvement est valide.
        if (estDiagonale) {
            if (damier.getListPion().get(positionArrivee) == null) {
                if (estTourBlanc && positionArrivee >= 1 && positionArrivee <= 5) {
                    pion = new Dames(Pion.Couleur.Blanc);
                    damier.ajouterDames(positionArrivee, pion, positionDepart);
                } else if (!estTourBlanc && positionArrivee >= 46 && positionArrivee <= 50) {
                    pion = new Dames(Pion.Couleur.Noir);
                    damier.ajouterDames(positionArrivee, pion, positionDepart);
                }
                int finalPos = getFinalPositionPrise(positionDepart, positionArrivee);
                Pion pionFinish = damier.getPion(finalPos);
                Pion pionArrivee = damier.getPion(positionArrivee);
                if (pionFinish == null && pionArrivee != null &&
                        pionArrivee.getCouleurPion() != pion.getCouleurPion()) {
                    List<Integer> mvt = mouvementsPossibles(positionDepart);
                    handleCaptureMouvementPossible(positionDepart, positionArrivee, mvt);
                    manouryHistoryBuilder(positionDepart, finalPos);
                    return;
                }
                manouryHistoryBuilder(positionDepart, positionArrivee);
                damier.ajouterPion(positionArrivee, pion);
                damier.retirerPion(positionDepart);
                estTourBlanc = !estTourBlanc;
            } else {
                throw new IllegalArgumentException("Mouvement impossible, case déjà prise.");
            }
        } else {
            throw new IllegalArgumentException("Mouvement impossible, mouvement diagonale seulement.");
        }
    }

    /**
     * Cette methode gere le mouvement des dames.
     *
     * @param positionDepart  Position depart de la dame.
     * @param positionArrivee Position arrive de la dame.
     * @param estDiagonale    boolean pour confirmer que le mouvement est diagonale.
     * @param pion            la Dame.
     */
    private void gererMouvementDame(int positionDepart, int positionArrivee,
                                    boolean estDiagonale, Pion pion) {
        int diffCol = Math.abs((positionArrivee % 10) - (positionDepart % 10));
        int diffRow = Math.abs((positionArrivee / 10) - (positionDepart / 10));
        if (diffCol == diffRow || estDiagonale) {
            if (damier.getListPion().get(positionArrivee) == null) {
                manouryHistoryBuilder(positionDepart, positionArrivee);
                damier.ajouterPion(positionArrivee, pion);
                damier.retirerPion(positionDepart);
                estTourBlanc = !estTourBlanc;
            } else {
                throw new IllegalArgumentException("Mouvement impossible, case déjà prise.");
            }
        } else {
            throw new IllegalArgumentException("Mouvement impossible");
        }
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

        if (verifierPriseValide(positionDepart, enemyPosition, positionArrivee)) {
            listePionsPris.add(new Object[]{damier.getListPion().get(enemyPosition), enemyPosition});

            StringBuilder sb = new StringBuilder();
            sb.append(positionDepart)
                    .append("x")
                    .append(positionArrivee);

            if (!estTourBlanc) {
                sb.append(")").insert(0, "(");
            }
            historiqueDeplacementDamier.add(sb.toString());
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
            positionDepart = Integer.parseInt(matcher.group(1));
            String separator = matcher.group(2);

            if (separator.equals("x")) {
                estPrise = true;
            }
            positionArrivee = Integer.parseInt(matcher.group(3));
        }

        // Determiner si le tour était au blanc ou noir et rétablie l'ancien tour
        determinerTour(dernierMouvement);

        Pion pion = new Pion();
        if (!estPrise) {
            retourMouvement(pion, positionArrivee, positionDepart);
        } else {
            retourPrise(pion, index, positionArrivee, positionDepart);
        }
        historiqueDeplacementDamier.remove(index);
    }

    /**
     * Fait le retour en arrière d'un mouvement.
     *
     * @param pion Prend en paramètre le pion bougé.
     * @param positionArrivee Prend en paramètre la posision d'arrivée.
     * @param positionDepart Prend en paramètre la position de départ.
     */
    private void retourMouvement(Pion pion, int positionArrivee, int positionDepart) {
        if (estTourBlanc) {
            pion = new Pion(Pion.Couleur.Blanc);
        } else {
            pion = new Pion(Pion.Couleur.Noir);
        }

        damier.ajouterPion(positionDepart, pion);
        damier.retirerPion(positionArrivee);
    }

    /**
     * Fait le retour en arrière d'une prise.
     *
     * @param pion Prend en paramètre le pion bougé.
     * @param index Prend en paramètre l'index du pion pris.
     * @param positionArrivee Prend en paramètre la posision d'arrivée.
     * @param positionDepart Prend en paramètre la position de départ.
     */
    private void retourPrise(Pion pion, int index, int positionArrivee, int positionDepart) {
        Object[] objectArray = listePionsPris.get(0);

        // Vérifie si la liste de prise n'est pas vide.
        if (objectArray != null && objectArray.length > 0) {
            Object firstElement = objectArray[0];

            if (firstElement instanceof Pion) {
                pion = (Pion) firstElement;
            }
        }

        int indexEnemy = listePionsPris.size() - 1;

        Pion pionEnemy = (Pion) listePionsPris.get(indexEnemy)[0];
        int positionEnemy = (int) listePionsPris.get(indexEnemy)[1];

        damier.ajouterPion(positionDepart, pion);
        damier.ajouterPion(positionEnemy, pionEnemy);
        damier.retirerPion(positionArrivee);

        listePionsPris.remove(index);
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
     * @param position La position du pion ou de la dame.
     * @return Une liste de mouvements possibles.
     */
    public List<Integer> mouvementsPossibles(int position) {
        List<Integer> mouvements = new ArrayList<>();
        Pion pion = damier.getListPion().get(position);
        if (pion == null) {
            return mouvements;
        }
        Direction[] decalages = getDirection(pion, position);

        for (Direction decalage : decalages) {
            evaluateDecalage(position, decalage, mouvements);
        }
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
        int index = (position % 10 >= 1 && position % 10 <= 5) ? 0 : 1;
        return switch (pion.getRepresentation()) {
            case 'P' -> (index == 0) ?
                    Direction.getDirectionBasStartBlanc() :
                    Direction.getDirectionBasStartNoir();
            case 'p' -> (index == 0) ?
                    Direction.getDirectionHautStartBlanc() :
                    Direction.getDirectionHautStartNoir();
            default -> (index == 0) ?
                    Direction.getDirectionStartBlanc() :
                    Direction.getDirectionStartNoir();
        };
    }

    /**
     * Évalue le décalage à partir d'une position donnée et ajoute les mouvements possibles.
     *
     * @param position   La position actuelle sur le damier.
     * @param decalage   Le décalage a évalué.
     * @param mouvements Une liste pour stocker les mouvements possibles.
     */
    private void evaluateDecalage(int position, Direction decalage, List<Integer> mouvements) {
        int nouvellePosition = position + decalage.getValue();

        if (!estPositionValide(nouvellePosition)) {
            return;
        }

        Pion currentPion = damier.getListPion().get(position);
        Pion targetPion = damier.getListPion().get(nouvellePosition);
        if (targetPion == null) {
            addPossibleMovements(nouvellePosition, decalage, mouvements);
        } else {
            assert currentPion != null;
            if (targetPion.getCouleurPion() != currentPion.getCouleurPion()) {
                int positionFinal = getFinalPositionPrise(position, nouvellePosition);
                if (damier.getPion(nouvellePosition + positionFinal) == null) {
                    //handleCaptureMouvementPossible(position, nouvellePosition, mouvements);
                    prisePion(position,nouvellePosition,nouvellePosition + positionFinal);
                    estTourBlanc = !estTourBlanc;
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
    private void addPossibleMovements(int nouvellePosition, Direction directionIndex,
                                      List<Integer> mouvements) {
        int positionInitiale = nouvellePosition - directionIndex.getValue();
        Pion pionInitiale = damier.getListPion().get(positionInitiale);
        String directionStart = directionIndex.toString();
        int directionMove = directionIndex.getValue();
        // Ce boolean est pour une alternance -1 ou + 1 dans le mouvement de diagonale de la dame.
        boolean alternance = false;

        if (pionInitiale != null && !estPionOuDame(positionInitiale)) {
            mouvements.add(nouvellePosition);
            // Si c'est faux, on exécute une loop pour la diagonale de la dame.
            while (estPositionValide(nouvellePosition)) {
                Pion pionPossibleNouvellePosition = damier.getListPion().get(nouvellePosition);
                if (borderBoard.contains(nouvellePosition)) {
                    break;
                }
                if (pionPossibleNouvellePosition == null) {
                    if (directionStart.contains("START_NOIR")) {
                        nouvellePosition += alternance ? directionMove : directionMove + 1;
                        alternance = !alternance;
                        mouvements.add(nouvellePosition);
                    } else {
                        nouvellePosition += alternance ? directionMove : directionMove - 1;
                        alternance = !alternance;
                        mouvements.add(nouvellePosition);
                    }
                } else {
                    break;
                }
            }
        }
        if (pionInitiale != null && estPionOuDame(positionInitiale)) {
            mouvements.add(nouvellePosition);
        }
    }

    /**
     * Cette methode verifie si le pion est une dame ou un pion.
     *
     * @param position position à vérifier.
     * @return boolean True si est un pion, false pour une dames
     */
    private boolean estPionOuDame(int position) {
        char i = damier.getListPion().get(position).getRepresentation();
        return switch (i) {
            case 'P', 'p' -> true;
            default -> false;
        };

    }

    /**
     * Traite un mouvement possible de capture.
     *
     * @param position         La position actuelle du pion.
     * @param nouvellePosition La position après le mouvement.
     * @param mouvements       Une liste pour stocker les mouvements possibles.
     */
    private void handleCaptureMouvementPossible(int position, int nouvellePosition, List<Integer> mouvements) {
        int index = getFinalPositionPrise(position, nouvellePosition);

        prisePion(position, nouvellePosition, nouvellePosition + index);
        mouvements.add(nouvellePosition);
        /*
        Pion pion = damier.getPion(position);
        Pion pionNouvellePosition = damier.getPion(nouvellePosition);
        if (damier.getPion(position).getCouleurPion() == Pion.Couleur.Blanc) {
            prisePion(nouvellePosition, position, nouvellePosition - index);
        } else {
            prisePion(nouvellePosition, position, nouvellePosition + index);
        }
        mouvements.add(nouvellePosition + index);

         */
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
                    if (estMouvementRestant(position)) {
                        cptMvtRestant++;
                    }
                }
            }
            return cptMvtRestant == 0;
        }
        return true;
    }

    /**
     * Vérifie s'il y a des mouvements possible pour la position
     *
     * @param position position a verifier.
     * @return boolean True si mouvement restants, false si aucun mouvement.
     */
    private boolean estMouvementRestant(int position) {
        return mouvementsPossibles(position).isEmpty();
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
         * Cette méthode statique renvoi toutes les valeurs d'un array de direction dans une List d'entier.
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