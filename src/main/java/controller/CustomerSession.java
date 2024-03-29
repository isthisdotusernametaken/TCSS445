package controller;

import java.math.BigDecimal;

import static controller.FunctionsAndProcedures.SUCCESS;

// This class stores the CustomerID and TransactionCart of a customer's current
// session and provides passthrough methods with these details filled in to (1)
// methods in FunctionsAndProcedures for services that use these details and
// (2) methods in TransactionCart for adding and removing items.
public class CustomerSession {

    private final int customerID;
    private final boolean online;
    private final TransactionCart cart;

    public CustomerSession(final int customerID, final boolean online) {
        this.customerID = customerID;
        this.online = online;
        cart = new TransactionCart();
    }

    public Object[][] viewCart() {
        var asLists = cart.getRows(); // o1 is chemicalIDs, o2 is quantities
        var asArrays = new Object[asLists.o1().size()][2];

        // Build 2D array with columns of (chemicalID, quantity)
        for (int i = 0; i < asLists.o1().size(); i++) {
            asArrays[i][0] = asLists.o1().get(i);
            asArrays[i][1] = asLists.o2().get(i);
        }

        return asArrays;
    }

    public void addItemToCart(final int chemicalID, final BigDecimal quantity) {
        cart.addRow(chemicalID, quantity);
    }

    public void removeItemFromCart(final int chemicalID) {
        cart.removeItem(chemicalID);
    }

    // S5
    // Returns new Object[]{message} on fail,
    // new Object[]{SUCCESS, Subtotal, TaxAmount} on success
    public Object[] completeTransaction(final String taxPercent, final Integer discountID) {
        var output = FunctionsAndProcedures.completeTransaction(
                customerID, taxPercent, discountID,
                cart, online
        );

        // If transaction succeeds, clear cart because products in it have been bought
        if (output[0] == SUCCESS)
            cart.clear();

        return output;
    }

    // S7
    public Object[][] viewPurchases(final int startPos, final int rowCnt,
                                    final boolean sortNewestFirst) {
        return FunctionsAndProcedures.viewPurchases(
                startPos, rowCnt,
                customerID, sortNewestFirst
        );
    }

    // S9
    public String reviewProduct(final int chemicalID, final int stars, final String text) {
        return FunctionsAndProcedures.reviewProduct(customerID, chemicalID, stars, text);
    }
}
