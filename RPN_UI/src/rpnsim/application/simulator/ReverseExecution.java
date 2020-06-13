package rpnsim.application.simulator;

import java.util.ArrayList;
import java.util.Set;

import javafx.scene.control.ListView;
import javafx.util.Pair;
import rpnsim.application.model.Arrow;
import rpnsim.application.model.Bond;
import rpnsim.application.model.LabelItem;
import rpnsim.application.model.Place;
import rpnsim.application.model.RPN;
import rpnsim.application.model.Transition;
import rpnsim.application.model.Token;


public class ReverseExecution extends Execution {
	

	public ReverseExecution(RPN rpn) {
		super(rpn);
	}
	

	
	public Set<Transition> getEnabledTransitions(){
		return null;
	}
	
	protected void deleteEffect(Transition t) {
		ArrayList<Arrow> incomingArrows = new ArrayList<>();
		ArrayList<Arrow> outgoingArrows = new ArrayList<>();
		for(Arrow arrow:t.getArrows()) {
			if(arrow.getSource() == t)
				outgoingArrows.add(arrow);
			if(arrow.getDestination() == t)
				incomingArrows.add(arrow);
		}
		
		//koita3e an dimiourgithike desmos me tin ektelesi tou transition
		ArrayList<LabelItem> bondsToDestroy = new ArrayList<>();
		RPNUtils utils = new RPNUtils(rpn);
		for(Arrow outgoingArrow: outgoingArrows) {
			Set<LabelItem> outgoingLabel = outgoingArrow.getSet();
					
			Set<LabelItem> outgoingBonds = utils.getBondsFrom(outgoingLabel);
			for(LabelItem outgoingBond: outgoingBonds) {
				boolean bondHasExisted = false;
				for(Arrow incomingArrow: incomingArrows) {
					Set<LabelItem> incomingLabel = incomingArrow.getSet();
					Set<LabelItem> incomingBonds = utils.getBondsFrom(incomingLabel);
					for(LabelItem incomingBond: incomingBonds) {
						if(outgoingBond.equals(incomingBond)) {
							bondHasExisted = true;
							break;
						}
					}
					if(bondHasExisted)
						break;
				}
				if(!bondHasExisted)
					bondsToDestroy.add(outgoingBond);
			}
					
		}
				
		//diagra4e tous desmous pou dimiourgithikan apo tin ektelesi tou transition
		for(LabelItem bond:bondsToDestroy) {
			Pair<Token,Token> pair = bond.getBond();
			Token tokenA = pair.getKey();
			Token tokenB = pair.getValue();
			for(Bond rpnBond:rpn.getBonds()) {
				Token source = rpnBond.getSource();
				Token destination = rpnBond.getDestination();
				if(tokenA==source && tokenB==destination) {
					tokenA.removeBond(destination);
					// tokenA.removeBond(rpnBond);
					break;
				}
				if(tokenB==source && tokenA==destination) {
					tokenB.removeBond(destination);
					// tokenA.removeBond(rpnBond);
					break;
				}
				
			}
		}
	}
	
	/**
	 * elegxw kata poso dimiourgithikan desmoi me tin forward ektelesi tou transition(diladi o desmos
	 * ypirxe mono sto outgoing arrow tou transition kai den ypirxe se kanena apo tan incoming arrows
	 * tou transition). An ontws dimiourgithikan desmoi me tin ektelesi tou transition tote tha prepei
	 * na tous katastre4w kai na metakinsw ola ta tokens/bonds pou yparxoun sta incoming arrows tou
	 * transition sta antistoixa incoming places.
	 * @param t
	 */
	public void fireTransition(Transition t) {
		
		deleteEffect(t);
		ArrayList<Arrow> incomingArrows = new ArrayList<>();
		for(Arrow arrow:t.getArrows()) {
			if(arrow.getDestination() == t)
				incomingArrows.add(arrow);
		}
		
		//metakina ta tokens pou briskontai sta incoming arrows sta antistoixa incoming places
		for(Arrow incomingArrow:incomingArrows) {
			Set<LabelItem> incomingLabel = incomingArrow.getSet();
			Place incomingPlace = (Place) incomingArrow.getSource();
			for(LabelItem item:incomingLabel) {
				if(item.isToken()) {
					Token token = item.getToken();
					incomingPlace.moveToken(token);
				}
				else {
					Pair<Token,Token> bond = item.getBond();
					incomingPlace.moveToken(bond.getKey());
					incomingPlace.moveToken(bond.getValue());
				}
			}
		}
	}
	
	public boolean isCausallyDependent(Transition t1, Transition t2) {
		
		if(t1.getHistoryList().isEmpty() || t2.getHistoryList().isEmpty())
			return false;
		
		int last_t1 = t1.getHistoryList().size()-1;
		int last_t2 = t2.getHistoryList().size()-1;
		int last_execution_t1 = t1.getHistoryList().get(last_t1);
		int last_execution_t2 = t2.getHistoryList().get(last_t2);
		
		RPNUtils utils = new RPNUtils(rpn);
		Set<LabelItem> post_t1 = utils.post(t1);
		Set<LabelItem> post_t2 = utils.post(t2);
		for(LabelItem item1:post_t1) {
			for(LabelItem item2:post_t2) {
				
				if(item1.equals(item2)) {
					if(last_execution_t1 < last_execution_t2)
						return true;
				}
				
				Token token1 = item1.getToken();
				Token token2 = item2.getToken();
				if(token1.isConnected(token2)) {
					if(last_execution_t1 < last_execution_t2)
						return true;
				}
				
			}
		}
		
		return false;
	}

}
