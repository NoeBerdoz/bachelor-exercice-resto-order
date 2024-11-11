package ch.hearc.ig.orderresto.presentation;

import ch.hearc.ig.orderresto.business.Order;
import ch.hearc.ig.orderresto.service.ProductOrderService;

public class MainCLI extends AbstractCLI {
    public void run() {
        this.ln("======================================================");
        this.ln("Que voulez-vous faire ?");
        this.ln("0. Quitter l'application");
        this.ln("1. Faire une nouvelle commande");
        this.ln("2. Consulter une commande");
        int userChoice = this.readIntFromUser(2);
        this.handleUserChoice(userChoice);
    }

    private void handleUserChoice(int userChoice) {
        if (userChoice == 0) {
            this.ln("Good bye!");
            return;
        }
        OrderCLI orderCLI = new OrderCLI();
        if (userChoice == 1) {
            Order newOrder = orderCLI.createNewOrder();
            ProductOrderService.getInstance().addOrderToRestaurant(newOrder);
        } else {
            Order existingOrder = orderCLI.selectOrder();
            if (existingOrder != null) {
                orderCLI.displayOrder(existingOrder);
            }
        }
        this.run();
    }
}
