package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.control.ListView;
import javafx.util.Pair;
import rpnsim.application.model.Arrow;
import rpnsim.application.model.LabelItem;
import rpnsim.application.model.Place;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Token;
import rpnsim.application.model.Transition;

public class OutOfCausalOrder extends ReverseExecution {

	public OutOfCausalOrder(RPN rpn) {
		super(rpn);
	}

	public Set<Transition> getEnabledTransitions() {
		enabledTransitions.clear();

		for (Transition transition : rpn.getTransitions()) {
			if (!transition.getHistoryList().isEmpty())
				enabledTransitions.add(transition);
		}

		return enabledTransitions;
	}

	private Transition last(Set<Token> component) {

		Set<LabelItem> componentSet = new HashSet<LabelItem>();
		for (Token token : component) {
			LabelItem item = new LabelItem(token);
			componentSet.add(item);
		}

		Transition last_executed = null;
		RPNUtils utils = new RPNUtils(rpn);
		for (Transition t : rpn.getTransitions()) {
			Set<LabelItem> post_t = utils.post(t);
			Set<LabelItem> intersection = utils.intersection(post_t, componentSet);
			if (!intersection.isEmpty() && !t.getHistoryList().isEmpty()) {
				if (last_executed != null) {
					int index = t.getHistoryList().size() - 1;
					int t_k = t.getHistoryList().get(index);
					index = last_executed.getHistoryList().size() - 1;
					int other_k = last_executed.getHistoryList().get(index);
					if (t_k > other_k)
						last_executed = t;
				} else
					last_executed = t;
			}
		}

		return last_executed;
	}

	public void fireTransition(Transition t) {
		deleteEffect(t);

		// history
		int index = t.getHistoryList().size() - 1;
		int k = t.getHistoryList().get(index);
		t.getHistoryList().remove(index);
		// t.updateHistory();
		for (Transition other : rpn.getTransitions()) {
			if (t != other) {
				ArrayList<Integer> otherHistory = other.getHistoryList();
				if (!(otherHistory.isEmpty())) {
					index = other.getHistoryList().size() - 1;
					int otherK = other.getHistoryList().get(index);
					if (otherK > k)
						otherHistory.set(index, otherK - 1);
				}
				// other.updateHistory();
			}
		}

		Set<Set<Token>> components = new HashSet<>();
		for (Place place : rpn.getPlaces()) {
			Token[] tokensIntoPlace = place.getTokens();
			for (Token token : tokensIntoPlace) {
				Boolean isPartOfComponent = false;
				for (Set<Token> component : components) {
					if (component.contains(token)) {
						isPartOfComponent = true;
						break;
					}
				}
				if (!isPartOfComponent) {
					Set<Token> component = token.getConnectedSet();
					components.add(component);
				}

			}
		}

		System.out.println("components: ");
		for (Set<Token> component : components) {
			for (Token token : component) {
				System.out.print(token.getName());
			}
			System.out.println();
		}
		System.out.println("--------------------- end of components");

		RPNUtils utils = new RPNUtils(rpn);
		for (Set<Token> component : components) {
			Transition last_executed = last(component);

			if (last_executed == null) {
				Token firstToken = component.iterator().next();
				firstToken.initial_place.moveToken(firstToken);
				continue;
			}

			System.out.println("last executed: " + last_executed.getName());

			for (Transition transition : rpn.getTransitions()) {
				if (last_executed.getName() == transition.getName()) {
					// prepei na metakinithei to component se ena apo ta outgoing places
					for (Arrow arrow : last_executed.getArrows()) {
						if (arrow.getSource() == last_executed) {
							Place outgoingPlace = (Place) arrow.getDestination();
							Set<LabelItem> arrowSet = arrow.getSet();
							Set<LabelItem> componentSet = new HashSet<LabelItem>();
							for (Token token : component) {
								LabelItem item = new LabelItem(token);
								componentSet.add(item);
							}
							Set<LabelItem> intersection = utils.intersection(arrowSet, componentSet);
							if (!intersection.isEmpty()) {
								outgoingPlace.moveToken(component.iterator().next());
							}
						}
					}
				}
			}

		}

	}

}
