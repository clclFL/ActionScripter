package club.pineclone.process.action;

import club.pineclone.api.CallBack;
import club.pineclone.process.api.RobotAction;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ActionList extends LinkedList<RobotAction> {

    public static final String HEAD = "head";
    public static final String TAIL = "tail";

    public void addHook(final String pos, CallBack<Void> hook) {
        if (isEmpty() || hook == null) return;
        switch (pos) {
            case HEAD: {
                addFirst(new ActionProxy(removeFirst() , hook));
                break;
            }
            case TAIL: {
                addLast(new ActionProxy(removeLast() , hook));
                break;
            }
        }
    }

    public static class ActionProxy extends RobotAction {

        private final RobotAction action;
        private CallBack<Void> hook;

        public ActionProxy(RobotAction action, CallBack<Void> callBack) {
            super(action.getDelay());
            this.action = action;
            this.hook = callBack;
        }

        public void setHook(CallBack<Void> hook) {
            this.hook = hook;
        }

        @Override
        public void act(Robot robot) {
            this.action.act(robot);
            this.hook.callBack(null);
        }

        @Deprecated
        public static void asTail(java.util.List<RobotAction> actions, CallBack<Void> hook) {
            if (actions.isEmpty()) return;
            int last = actions.size() - 1;
            RobotAction action = actions.remove(last);
            actions.add(new ActionProxy(action, hook));
        }

        /**
         * This method require the actions has been set by the method {@link ActionProxy#asTail(java.util.List, CallBack)}, if not,
         * this method will simply return and do nothing.
         *
         * @param actions the description action list, this method will set the tail action hook of this list.
         * @param hook    the new call back function about to set.
         */
        @Deprecated
        public static void setTail(java.util.List<RobotAction> actions, CallBack<Void> hook) {
            if (actions.isEmpty()) return;
            RobotAction robotAction = actions.get(actions.size() - 1);
            if (!(robotAction instanceof ActionProxy)) return;
            ((ActionProxy) robotAction).setHook(hook);
        }

        @Deprecated
        public static void asHead(java.util.List<RobotAction> actions, CallBack<Void> hook) {
            if (actions.isEmpty()) return;
            int first = 0;
            RobotAction action = actions.remove(first);
            actions.add(0, new ActionProxy(action, hook));
        }

        /**
         * This method is similar to the method {@link ActionProxy#setTail(java.util.List, CallBack)}, this method will allow you to
         * reset the head action in the action list, to call this method, as the same as the last method, this action list
         * is required to be description as an "hook action list".
         *
         * @param actions
         * @param hook
         */
        @Deprecated
        public static void setHead(List<RobotAction> actions, CallBack<Void> hook) {
            if (actions.isEmpty()) return;
            RobotAction robotAction = actions.get(0);
            if (robotAction instanceof ActionProxy) {
                ((ActionProxy) robotAction).setHook(hook);
            }
        }
    }
}
