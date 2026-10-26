package com.mycompany.gamezone.ui;

/**
 * One option of the main menu: the number the user types, the text shown
 * in the menu and the action it runs.
 */
class MenuOption {

    private final int number;
    private final String label;
    private final Runnable action;

    /**
     * Creates a menu option.
     *
     * @param number number the user types to choose it
     * @param label  Spanish text shown in the menu
     * @param action action run when the option is chosen
     */
    MenuOption(int number, String label, Runnable action) {
        this.number = number;
        this.label = label;
        this.action = action;
    }

    int getNumber() {
        return number;
    }

    String getLabel() {
        return label;
    }

    void run() {
        action.run();
    }
}
