package classes.stateofAutoAGV;

import classes.AutoAgv;
import constant.Constant;
import classes.StateOfNode2D;

public class RunningState extends HybridState {
    public boolean _isLastMoving;
    private boolean _agvIsDestroyed;

    public RunningState(boolean _isLastMoving) {

        this._isLastMoving = _isLastMoving;
        this._agvIsDestroyed = false;
    }

    public void move(AutoAgv agv) {
        if (this._agvIsDestroyed) 
            return;
        if (agv.path == null) {
            return;
        }
        if (agv.cur == agv.path.length - 1) {
            agv.setVelocity(0, 0);
            if (this._isLastMoving) {
                agv.curNode.setState(StateOfNode2D.EMPTY);
                this._agvIsDestroyed = true;
                agv.scene.remove(agv);
                return;
            } else {
                agv.hybridState = new IdleState(Constant.getTime());
            }
            return;
        }
        if (agv.cur + 1 >= agv.path.length) {
            System.out.println("Loi roi do: " + (agv.cur + 1));
        }
        var nodeNext = agv.graph.nodes[agv.path[agv.cur + 1].x][agv.path[agv.cur + 1].y];
        var shortestDistance = Constant.minDistance(agv, agv.collidedActors);

        if (nodeNext.state == StateOfNode2D.BUSY || shortestDistance < Constant.SAFE_DISTANCE()) {
            agv.setVelocity(0, 0);
            if (agv.waitT > 0)
                return;
            agv.waitT = Constant.getTime();
  
        } else {
      
            if (shortestDistance >= Constant.SAFE_DISTANCE()) {
                agv.collidedActors.clear();
            }
      
            if (agv.waitT > 0) {
                agv.curNode.setU((Constant.getTime() - agv.waitT) / 1000);
             
                agv.waitT = 0;
            }

            if (agv.x != nodeNext.x * 32 || agv.y != nodeNext.y * 32) {
                agv.moveTo(nodeNext.x * 32, nodeNext.y * 32, 1);
            } else {
      
                agv.curNode.setState(StateOfNode2D.EMPTY);
                agv.curNode = nodeNext;
                agv.curNode.setState(StateOfNode2D.BUSY);
                agv.cur++;
                agv.setVelocity(0, 0);
                agv.sobuocdichuyen++;
      
                if (agv.sobuocdichuyen % 10 == 0 || Constant.getTime() - agv.thoigiandichuyen > 10000) {
                    agv.thoigiandichuyen = Constant.getTime();
                    agv.cur = 0;
                    agv.path = agv.calPathAStar(agv.curNode, agv.endNode);
                }
            }
        }

    }
}
