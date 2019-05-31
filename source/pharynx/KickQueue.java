/*
 * KickQueue.java
 *
 * Created on December 20, 2000, 11:50 AM
 */

package pharynx;
import java.util.*;

/**
 *
 * @author  leon@eatworms.swmed.edu
 * @version 0.1
 */
public class KickQueue extends java.lang.Object {
    /*
     * So that it can hold multiple Kicks with identical times, and possibly
     * even indentical in all other respects, the queue is implemented as a
     * SortedMap whose keys are the times, and whose values are Maps of Kicks.
     * The keys for the Maps are unique tickets assigned to the Kicks when
     * they're queued.
     */
    private SortedMap queue;
    private long lastTicket = 0;
    private SortedMap ticketMap;

    public class Ticket implements Comparable {
        private long n = 0;
        Ticket(long n) {
            this.n = n;
        }

        public int compareTo(Object o2) {
            Ticket t2 = (Ticket) o2;
            if (n < t2.n) return -1;
            else if (n > t2.n) return 1;
            else return 0;
        }
        
    }

    /** Creates new KickQueue */
    public KickQueue() {
        queue = new TreeMap();
        ticketMap = new TreeMap();
    }

    /** Creates new KickQueue, loading an initial Kick */
    public KickQueue(Kick k) {
        this();
        add(k);
    }

    /** Creates new KickQueue, loading a list of initial Kicks */
    public KickQueue(List klist) {
        this();
        for(Iterator i = klist.listIterator(); i.hasNext();) {
            add((Kick) i.next());
        }
    }

    /** Find out whether there is anything in the queue */
    public boolean isEmpty() {
        return ticketMap.isEmpty();
    }

    /** add a Kick to the queue
     *
     * @param k     the Kick to add
     * @return      the return value is a unique Ticket that can be used to
     *              access this queue element again.
     */
    public Ticket add(Kick k) {
        Double t = new Double(k.time());
        Ticket tk = new Ticket(++lastTicket);
        ticketMap.put(tk, t);
        Map klist = (Map) queue.get(t);
        if (klist == null) {
            klist = new TreeMap();
            queue.put(t, klist);
        }
        klist.put(tk, k);
        return(tk);
    }

    /** retrieve and remove the first (earlest time) Kick */
    public Kick first() {
        Map kl = findFirst();
        if (null == kl) return null;
        Iterator i = kl.entrySet().iterator();
        Map.Entry ke = (Map.Entry) i.next();
        i.remove();
        Ticket tk = (Ticket) ke.getKey();
        Kick k = (Kick) ke.getValue();
        ticketMap.remove(tk);
        return k;
    }

    /** retrieve and remove the first (earliest time) Kick */
    public Kick remove() {
        return first();
    }

    /** retrieve and remove the Kick with Ticket tk */
    public Kick remove(Ticket tk) {
        Double t = (Double) ticketMap.get(tk);
        if (null == t) return null;
        Map kl = (Map) queue.get(t);
        Kick k = (Kick) kl.get(tk);
        kl.remove(tk);
        ticketMap.remove(tk);
        return k;
    }

    public Kick peek() {
        Map kl = findFirst();
        if (null == kl) return null;
        return (Kick) ((Map.Entry) kl.entrySet().iterator().next()).getValue();
    }

    public Kick peek(Ticket tk) {
        Map kl = (Map) ticketMap.get(tk);
        if (null == kl) return null;
        return (Kick) kl.get(tk);
    }

    private Map findFirst() {
        Double t;
        Map kl = null;
        for(; !queue.isEmpty();) {
            t = (Double) queue.firstKey();
            kl = (Map) queue.get(t);
            if ((kl != null) && !kl.isEmpty()) break;
            queue.remove(t);
        }
        return kl;
    }

    private Ticket findFirstTicket() {
        Map kl = findFirst();
        if (null == kl) return null;
        return (Ticket) ((Map.Entry) kl.entrySet().iterator().next()).getKey();
    }
    
    private void sizeCheck() {
        int s1 = ticketMap.size();
        int s2 = 0;
        for(Iterator i = queue.values().iterator(); i.hasNext();) {
            Object n = i.next();
            Map kl = (Map) n;
            int st = kl.size();
            s2 += st;
        }
        if (s1 != s2) {
            throw new CantHappenException("inconsistent maps in KickQueue");
        }
    }
}
