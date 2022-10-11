package com.dynamic_validate;

import com.dynamic_validate.data.Data;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TestJava {

    @Test
    public void testList() {
        List<Integer> list = new ArrayList<>();
        int a = 3;
        Integer b = 3;
        list.add(a);
        if (list.contains(b)) {
            list.add(4);
        }
        System.out.println(list);
    }

    @Test
    public void testFile() {
        PrintStream ps = null;
        try {
            ps = new PrintStream(Data.RepTxt + 1 + ".txt");
            System.setOut(ps);
            System.out.println("这条算啥");
            ps.close();
        } catch (FileNotFoundException e) {
            ps.close();
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String s = "path,qstr,inode,int,filename,nameidata,atomic_t,kuid_t,kgid_t,int,kernel_cap_struct,user_struct,user_namespace,group_info,rcu_head,int,seqcount_t,hlist_bl_node,dentry,qstr,inode,char,lockref,dentry_operations,super_block,long,void,list_head,11057,path,inode,file_operations,spinlock,rw_hint,atomic_t,int,mutex,long,fown_struct,cred,file_ra_state,void,address_space,char,int,audit_names,vfsmount,dentry";
        s = "include/linux/string.h,include/linux/mm.h,include/linux/file.h,include/linux/fdtable.h,include/linux/fsnotify.h,include/linux/module.h,include/linux/tty.h,include/linux/namei.h,include/linux/backing-dev.h,include/linux/capability.h,include/linux/securebits.h,include/linux/security.h,include/linux/mount.h,include/linux/fcntl.h,include/linux/slab.h,include/linux/uaccess.h,include/linux/fs.h,include/linux/personality.h,include/linux/pagemap.h,include/linux/syscalls.h,include/linux/rcupdate.h,include/linux/audit.h,include/linux/falloc.h,include/linux/fs_struct.h,include/linux/ima.h,include/linux/dnotify.h,include/linux/compat.h,fs/internal.h,include/linux/compiler.h,include/linux/types.h,include/linux/stddef.h,include/stdarg.h,include/uapi/linux/string.h,include/asm/string.h,include/linux/errno.h,include/linux/compiler.h,include/linux/types.h,include/linux/posix_types.h,include/linux/posix_types.h,include/linux/compiler.h,include/linux/spinlock.h,include/linux/nospec.h,include/linux/types.h,include/linux/init.h,include/linux/atomic.h,include/linux/fsnotify_backend.h,include/linux/bug.h,include/linux/list.h,include/linux/stat.h,include/linux/compiler.h,include/linux/cache.h,include/linux/kmod.h,include/linux/init.h,include/linux/elf.h,include/linux/stringify.h,include/linux/kobject.h,include/linux/moduleparam.h,include/linux/jump_label.h,include/linux/export.h,include/linux/rbtree_latch.h,include/linux/error-injection.h,include/linux/percpu.h,include/asm/module.h,include/linux/major.h,include/linux/termios.h,include/linux/workqueue.h,include/linux/tty_driver.h,include/linux/tty_ldisc.h,include/linux/mutex.h,include/linux/tty_flags.h,include/linux/seq_file.h,include/uapi/linux/tty.h,include/linux/rwsem.h,include/linux/llist.h,include/linux/kernel.h,include/linux/path.h,include/linux/errno.h,include/linux/kernel.h,include/linux/sched.h,include/linux/blkdev.h,include/linux/writeback.h,include/linux/blk-cgroup.h,include/linux/backing-dev-defs.h,include/uapi/linux/capability.h,include/uapi/linux/securebits.h,include/linux/key.h,include/linux/err.h,include/linux/types.h,include/linux/list.h,include/linux/nodemask.h,include/linux/spinlock.h,include/linux/seqlock.h,include/linux/atomic.h,include/uapi/linux/fcntl.h,include/linux/gfp.h,include/linux/overflow.h,include/linux/types.h,include/linux/workqueue.h,include/linux/kasan.h,include/linux/linkage.h,include/linux/wait_bit.h,include/linux/kdev_t.h,include/linux/dcache.h,include/linux/path.h,include/linux/stat.h,include/linux/cache.h,include/linux/list.h,include/linux/list_lru.h,include/linux/llist.h,include/linux/radix-tree.h,include/linux/xarray.h,include/linux/rbtree.h,include/linux/init.h,include/linux/pid.h,include/linux/bug.h,include/linux/mutex.h,include/linux/rwsem.h,include/linux/mm_types.h,include/linux/semaphore.h,include/linux/fiemap.h,include/linux/rculist_bl.h,include/linux/atomic.h,include/linux/shrinker.h,include/linux/migrate_mode.h,include/linux/uidgid.h,include/linux/lockdep.h,include/linux/percpu-rwsem.h,include/linux/workqueue.h,include/linux/delayed_call.h,include/linux/uuid.h,include/linux/errseq.h,include/linux/ioprio.h,include/asm/byteorder.h,include/uapi/linux/fs.h,include/linux/quota.h,include/linux/nfs_fs_i.h,include/linux/err.h,include/uapi/linux/personality.h,include/linux/list.h,include/linux/highmem.h,include/linux/compiler.h,include/linux/gfp.h,include/linux/bitops.h,include/linux/hardirq.h,include/linux/hugetlb_inline.h,include/linux/types.h,include/linux/aio_abi.h,include/linux/signal.h,include/linux/list.h,include/linux/bug.h,include/linux/sem.h,include/asm/siginfo.h,include/linux/unistd.h,include/linux/quota.h,include/linux/key.h,include/trace/syscall.h,include/linux/types.h,include/linux/compiler.h,include/linux/atomic.h,include/linux/irqflags.h,include/linux/preempt.h,include/linux/bottom_half.h,include/linux/lockdep.h,include/asm/processor.h,include/linux/cpumask.h,include/linux/sched.h,include/linux/ptrace.h,include/uapi/linux/audit.h,include/uapi/linux/falloc.h,include/linux/path.h,include/linux/spinlock.h,include/linux/seqlock.h,include/linux/kexec.h,include/linux/types.h,include/linux/compat_time.h,include/linux/stat.h,include/linux/param.h,include/linux/sem.h,include/linux/socket.h,include/linux/if.h,include/linux/aio_abi.h,include/linux/unistd.h,include/asm/compat.h,include/linux/compiler_types.h,include/uapi/linux/types.h,include/uapi/linux/stddef.h,include/uapi/linux/errno.h,include/linux/compiler_types.h,include/uapi/linux/types.h,include/linux/compiler_types.h,include/linux/typecheck.h,include/linux/thread_info.h,include/asm/barrier.h,include/linux/spinlock_types.h,include/linux/rwlock.h,include/asm/barrier.h,include/uapi/linux/types.h,include/asm/atomic.h,include/asm/barrier.h,include/asm-generic/atomic-long.h,include/asm/bug.h,include/linux/build_bug.h,include/linux/poison.h,include/linux/.h,include/asm/stat.h,include/uapi/linux/stat.h,include/linux/time.h,include/linux/compiler_types.h,include/uapi/linux/kernel.h,include/asm/cache.h,include/asm/elf.h,include/uapi/linux/elf.h,include/linux/sysfs.h,include/linux/kref.h,include/linux/kobject_ns.h,include/linux/wait.h,include/linux/mmdebug.h,include/linux/smp.h,include/linux/printk.h,include/linux/pfn.h,include/asm/percpu.h,include/linux/timer.h,include/linux/threads.h,include/linux/cdev.h,include/linux/wait.h,include/asm/current.h,include/linux/spinlock_types.h,include/linux/osq_lock.h,include/linux/debug_locks.h,include/linux/cred.h,include/linux/log2.h,include/linux/typecheck.h,include/linux/printk.h,include/linux/build_bug.h,include/uapi/linux/kernel.h,include/uapi/linux/errno.h,include/linux/log2.h,include/linux/typecheck.h,include/linux/printk.h,include/linux/build_bug.h,include/uapi/linux/kernel.h,include/uapi/linux/sched.h,include/asm/current.h,include/linux/shm.h,include/linux/kcov.h,include/linux/plist.h,include/linux/hrtimer.h,include/linux/seccomp.h,include/linux/resource.h,include/linux/latencytop.h,include/linux/sched/prio.h,include/linux/signal_types.h,include/linux/mm_types_task.h,include/linux/task_io_accounting.h,include/linux/rseq.h,include/linux/sched/clock.h,include/linux/flex_proportions.h,include/linux/blk_types.h,include/linux/cgroup.h,include/linux/percpu_counter.h,include/linux/kthread.h,include/linux/percpu_counter.h,include/linux/percpu-refcount.h,include/linux/flex_proportions.h,include/linux/timer.h,include/linux/kref.h,include/linux/sysctl.h,include/linux/assoc_array.h,include/linux/refcount.h,include/linux/time64.h,include/asm/errno.h,include/uapi/linux/types.h,include/linux/poison.h,include/linux/.h,include/linux/threads.h,include/linux/bitmap.h,include/linux/numa.h,include/linux/typecheck.h,include/linux/thread_info.h,include/asm/barrier.h,include/linux/spinlock_types.h,include/linux/rwlock.h,include/asm/atomic.h,include/asm/barrier.h,include/asm-generic/atomic-long.h,include/asm/fcntl.h,include/linux/mmdebug.h,include/linux/mmzone.h,include/linux/topology.h,include/uapi/linux/types.h,include/linux/timer.h,include/linux/threads.h,include/linux/compiler_types.h,include/asm/linkage.h,include/linux/wait.h,include/uapi/linux/kdev_t.h,include/linux/rculist.h,include/linux/lockref.h,include/linux/stringhash.h,include/linux/wait.h,include/asm/stat.h,include/uapi/linux/stat.h,include/linux/time.h,include/uapi/linux/kernel.h,include/asm/cache.h,include/linux/poison.h,include/linux/.h,include/linux/rculist.h,include/asm/bug.h,include/linux/build_bug.h,include/asm/current.h,include/linux/spinlock_types.h,include/linux/osq_lock.h,include/linux/debug_locks.h,include/linux/mm_types_task.h,include/linux/auxvec.h,include/linux/completion.h,include/linux/uprobes.h,include/linux/page-flags-layout.h,include/asm/mmu.h,include/linux/list_bl.h,include/asm/atomic.h,include/asm/barrier.h,include/asm-generic/atomic-long.h,include/linux/highuid.h,include/linux/rcuwait.h,include/linux/rcu_sync.h,include/linux/timer.h,include/linux/threads.h,include/uapi/linux/uuid.h,include/linux/sched/rt.h,include/linux/iocontext.h,include/linux/limits.h,include/linux/ioctl.h,include/asm/errno.h,include/linux/poison.h,include/linux/.h,include/asm/cacheflush.h,include/asm/kmap_types.h,include/linux/compiler_types.h,include/linux/mmdebug.h,include/linux/mmzone.h,include/linux/topology.h,include/asm/types.h,include/asm/bitops.h,include/linux/ftrace_irq.h,include/linux/vtime.h,include/asm/hardirq.h,include/uapi/linux/types.h,include/linux/signal_types.h,include/linux/poison.h,include/linux/.h,include/asm/bug.h,include/linux/build_bug.h,include/uapi/linux/sem.h,include/linux/sysctl.h,include/linux/assoc_array.h,include/linux/refcount.h,include/linux/time64.h,include/linux/tracepoint.h,include/linux/trace_events.h,include/linux/thread_info.h,include/asm/ptrace.h,include/uapi/linux/types.h,include/linux/compiler_types.h,include/asm/atomic.h,include/asm/barrier.h,include/asm-generic/atomic-long.h,include/linux/typecheck.h,include/asm/irqflags.h,include/asm/preempt.h,include/linux/threads.h,include/linux/bitmap.h,include/uapi/linux/sched.h,include/asm/current.h,include/linux/shm.h,include/linux/kcov.h,include/linux/plist.h,include/linux/hrtimer.h,include/linux/seccomp.h,include/linux/resource.h,include/linux/latencytop.h,include/linux/sched/prio.h,include/linux/signal_types.h,include/linux/mm_types_task.h,include/linux/task_io_accounting.h,include/linux/rseq.h,include/linux/sched/signal.h,include/linux/pid_namespace.h,include/uapi/linux/ptrace.h,include/linux/elf-em.h,include/linux/typecheck.h,include/linux/thread_info.h,include/asm/barrier.h,include/linux/spinlock_types.h,include/linux/rwlock.h,include/uapi/linux/types.h,include/linux/time64.h,include/asm/stat.h,include/uapi/linux/stat.h,include/linux/time.h,include/uapi/linux/sem.h,include/asm/socket.h,include/linux/sockios.h,include/linux/uio.h,include/uapi/linux/socket.h,include/linux/restart_block.h,include/asm/thread_info.h,include/linux/rwlock_types.h,include/linux/sysinfo.h,include/linux/kernfs.h,include/uapi/linux/wait.h,include/linux/ktime.h,include/linux/debugobjects.h,include/linux/device.h,include/uapi/linux/wait.h,include/linux/rwlock_types.h,include/linux/selinux.h,include/linux/sched/user.h,include/linux/sysinfo.h,include/linux/sysinfo.h,include/asm/page.h,include/uapi/linux/shm.h,include/asm/shmparam.h,include/uapi/linux/kcov.h,include/linux/ktime.h,include/linux/timerqueue.h,include/uapi/linux/seccomp.h,include/uapi/linux/resource.h,include/uapi/linux/signal.h,include/asm/page.h,include/linux/bvec.h,include/linux/ktime.h,include/linux/cgroupstats.h,include/linux/kernfs.h,include/linux/ns_common.h,include/linux/nsproxy.h,include/linux/user_namespace.h,include/linux/kernel_stat.h,include/linux/cgroup-defs.h,include/linux/ktime.h,include/linux/debugobjects.h,include/uapi/linux/sysctl.h,include/linux/math64.h,include/uapi/linux/time.h,include/linux/restart_block.h,include/asm/thread_info.h,include/linux/rwlock_types.h,include/asm/topology.h,include/linux/ktime.h,include/linux/debugobjects.h,include/uapi/linux/wait.h,include/generated/bounds.h,include/linux/hash.h,include/uapi/linux/wait.h,include/linux/sysinfo.h,include/linux/rwlock_types.h,include/asm/page.h,include/uapi/linux/auxvec.h,include/generated/bounds.h,include/linux/bit_spinlock.h,include/linux/ktime.h,include/linux/debugobjects.h,include/asm/topology.h,include/linux/context_tracking_state.h,include/uapi/linux/signal.h,include/linux/ipc.h,include/asm/sembuf.h,include/uapi/linux/sysctl.h,include/linux/math64.h,include/uapi/linux/time.h,include/linux/tracepoint-defs.h,include/linux/ring_buffer.h,include/linux/trace_seq.h,include/linux/perf_event.h,include/linux/restart_block.h,include/asm/thread_info.h,include/asm/page.h,include/uapi/linux/shm.h,include/asm/shmparam.h,include/uapi/linux/kcov.h,include/linux/ktime.h,include/linux/timerqueue.h,include/uapi/linux/seccomp.h,include/uapi/linux/resource.h,include/uapi/linux/signal.h,include/asm/page.h,include/linux/sched/jobctl.h,include/linux/sched/task.h,include/linux/nsproxy.h,include/linux/ns_common.h,include/linux/idr.h,include/linux/restart_block.h,include/asm/thread_info.h,include/linux/rwlock_types.h,include/linux/math64.h,include/uapi/linux/time.h,include/linux/ipc.h,include/asm/sembuf.h,include/uapi/linux/uio.h,include/linux/jiffies.h,include/linux/ioport.h,include/linux/klist.h,include/linux/pm.h,include/linux/ratelimit.h,include/asm/device.h,include/linux/pm_wakeup.h,include/linux/ratelimit.h,include/asm-generic/hugetlb_encode.h,include/asm/shmbuf.h,include/linux/jiffies.h,include/asm/resource.h,include/asm/signal.h,include/linux/jiffies.h,include/linux/interrupt.h,include/asm/irq.h,include/linux/u64_stats_sync.h,include/linux/bpf-cgroup.h,include/linux/jiffies.h,include/asm/div64.h,include/linux/jiffies.h,include/asm/auxvec.h,include/linux/jiffies.h,include/linux/static_key.h,include/asm/signal.h,include/linux/rhashtable.h,include/uapi/linux/ipc.h,include/asm/div64.h,include/linux/static_key.h,include/linux/poll.h,include/linux/seq_buf.h,include/uapi/linux/perf_event.h,include/uapi/linux/bpf_perf_event.h,include/linux/ftrace.h,include/linux/cpu.h,include/linux/irq_work.h,include/linux/static_key.h,include/linux/jump_label_ratelimit.h,include/linux/perf_regs.h,include/asm/local.h,include/asm-generic/hugetlb_encode.h,include/asm/shmbuf.h,include/linux/jiffies.h,include/asm/resource.h,include/asm/signal.h,include/asm/div64.h,include/linux/rhashtable.h,include/uapi/linux/ipc.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/irqreturn.h,include/linux/irqnr.h,include/asm/sections.h,include/uapi/linux/bpf.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/jhash.h,include/linux/list_nulls.h,include/asm/ipcbuf.h,include/uapi/linux/poll.h,include/uapi/linux/eventpoll.h,include/linux/trace_clock.h,include/linux/kallsyms.h,include/asm/ftrace.h,include/linux/node.h,include/linux/cpuhotplug.h,include/linux/sched/task_stack.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/jhash.h,include/linux/list_nulls.h,include/asm/ipcbuf.h,include/uapi/linux/timex.h,include/asm/timex.h,include/uapi/linux/timex.h,include/asm/timex.h,include/uapi/linux/timex.h,include/asm/timex.h,include/uapi/linux/irqnr.h,include/uapi/linux/timex.h,include/asm/timex.h,include/uapi/linux/timex.h,include/asm/timex.h,include/uapi/linux/timex.h,include/asm/timex.h,include/linux/unaligned/packed_struct.h,include/asm/poll.h,include/asm/trace_clock.h,include/linux/magic.h,include/uapi/linux/timex.h,include/asm/timex.h,include/linux/unaligned/packed_struct.h";
        s = "include/linux/string.h,include/linux/mm.h,include/linux/file.h,include/linux/fdtable.h,include/linux/fsnotify.h,include/linux/module.h,include/linux/tty.h,include/linux/namei.h,include/linux/backing-dev.h,include/linux/capability.h,include/linux/securebits.h,include/linux/security.h,include/linux/mount.h,include/linux/fcntl.h,include/linux/slab.h,include/linux/uaccess.h,include/linux/fs.h,include/linux/personality.h,include/linux/pagemap.h,include/linux/syscalls.h,include/linux/rcupdate.h,include/linux/audit.h,include/linux/falloc.h,include/linux/fs_struct.h,include/linux/ima.h,include/linux/dnotify.h,include/linux/compat.h,fs/internal.h,include/linux/compiler.h,include/linux/types.h,include/linux/stddef.h,include/stdarg.h,include/uapi/linux/string.h,include/asm/string.h,include/linux/errno.h,include/linux/posix_types.h,include/linux/spinlock.h,include/linux/nospec.h,include/linux/init.h,include/linux/atomic.h,include/linux/fsnotify_backend.h,include/linux/bug.h,include/linux/list.h,include/linux/stat.h,include/linux/cache.h,include/linux/kmod.h,include/linux/elf.h,include/linux/stringify.h,include/linux/kobject.h,include/linux/moduleparam.h,include/linux/jump_label.h,include/linux/export.h,include/linux/rbtree_latch.h,include/linux/error-injection.h,include/linux/percpu.h,include/asm/module.h,include/linux/major.h,include/linux/termios.h,include/linux/workqueue.h,include/linux/tty_driver.h,include/linux/tty_ldisc.h,include/linux/mutex.h,include/linux/tty_flags.h,include/linux/seq_file.h,include/uapi/linux/tty.h,include/linux/rwsem.h,include/linux/llist.h,include/linux/kernel.h,include/linux/path.h,include/linux/sched.h,include/linux/blkdev.h,include/linux/writeback.h,include/linux/blk-cgroup.h,include/linux/backing-dev-defs.h,include/uapi/linux/capability.h,include/uapi/linux/securebits.h,include/linux/key.h,include/linux/err.h,include/linux/nodemask.h,include/linux/seqlock.h,include/uapi/linux/fcntl.h,include/linux/gfp.h,include/linux/overflow.h,include/linux/kasan.h,include/linux/linkage.h,include/linux/wait_bit.h,include/linux/kdev_t.h,include/linux/dcache.h,include/linux/list_lru.h,include/linux/radix-tree.h,include/linux/xarray.h,include/linux/rbtree.h,include/linux/pid.h,include/linux/mm_types.h,include/linux/semaphore.h,include/linux/fiemap.h,include/linux/rculist_bl.h,include/linux/shrinker.h,include/linux/migrate_mode.h,include/linux/uidgid.h,include/linux/lockdep.h,include/linux/percpu-rwsem.h,include/linux/delayed_call.h,include/linux/uuid.h,include/linux/errseq.h,include/linux/ioprio.h,include/asm/byteorder.h,include/uapi/linux/fs.h,include/linux/quota.h,include/linux/nfs_fs_i.h,include/uapi/linux/personality.h,include/linux/highmem.h,include/linux/bitops.h,include/linux/hardirq.h,include/linux/hugetlb_inline.h,include/linux/aio_abi.h,include/linux/signal.h,include/linux/sem.h,include/asm/siginfo.h,include/linux/unistd.h,include/trace/syscall.h,include/linux/irqflags.h,include/linux/preempt.h,include/linux/bottom_half.h,include/asm/processor.h,include/linux/cpumask.h,include/linux/ptrace.h,include/uapi/linux/audit.h,include/uapi/linux/falloc.h,include/linux/kexec.h,include/linux/compat_time.h,include/linux/param.h,include/linux/socket.h,include/linux/if.h,include/asm/compat.h,include/linux/compiler_types.h,include/uapi/linux/types.h,include/uapi/linux/stddef.h,include/uapi/linux/errno.h,include/linux/typecheck.h,include/linux/thread_info.h,include/asm/barrier.h,include/linux/spinlock_types.h,include/linux/rwlock.h,include/asm/atomic.h,include/asm-generic/atomic-long.h,include/asm/bug.h,include/linux/build_bug.h,include/linux/poison.h,include/linux/.h,include/asm/stat.h,include/uapi/linux/stat.h,include/linux/time.h,include/uapi/linux/kernel.h,include/asm/cache.h,include/asm/elf.h,include/uapi/linux/elf.h,include/linux/sysfs.h,include/linux/kref.h,include/linux/kobject_ns.h,include/linux/wait.h,include/linux/mmdebug.h,include/linux/smp.h,include/linux/printk.h,include/linux/pfn.h,include/asm/percpu.h,include/linux/timer.h,include/linux/threads.h,include/linux/cdev.h,include/asm/current.h,include/linux/osq_lock.h,include/linux/debug_locks.h,include/linux/cred.h,include/linux/log2.h,include/uapi/linux/sched.h,include/linux/shm.h,include/linux/kcov.h,include/linux/plist.h,include/linux/hrtimer.h,include/linux/seccomp.h,include/linux/resource.h,include/linux/latencytop.h,include/linux/sched/prio.h,include/linux/signal_types.h,include/linux/mm_types_task.h,include/linux/task_io_accounting.h,include/linux/rseq.h,include/linux/sched/clock.h,include/linux/flex_proportions.h,include/linux/blk_types.h,include/linux/cgroup.h,include/linux/percpu_counter.h,include/linux/kthread.h,include/linux/percpu-refcount.h,include/linux/sysctl.h,include/linux/assoc_array.h,include/linux/refcount.h,include/linux/time64.h,include/asm/errno.h,include/linux/bitmap.h,include/linux/numa.h,include/asm/fcntl.h,include/linux/mmzone.h,include/linux/topology.h,include/asm/linkage.h,include/uapi/linux/kdev_t.h,include/linux/rculist.h,include/linux/lockref.h,include/linux/stringhash.h,include/linux/auxvec.h,include/linux/completion.h,include/linux/uprobes.h,include/linux/page-flags-layout.h,include/asm/mmu.h,include/linux/list_bl.h,include/linux/highuid.h,include/linux/rcuwait.h,include/linux/rcu_sync.h,include/uapi/linux/uuid.h,include/linux/sched/rt.h,include/linux/iocontext.h,include/linux/limits.h,include/linux/ioctl.h,include/asm/cacheflush.h,include/asm/kmap_types.h,include/asm/types.h,include/asm/bitops.h,include/linux/ftrace_irq.h,include/linux/vtime.h,include/asm/hardirq.h,include/uapi/linux/sem.h,include/linux/tracepoint.h,include/linux/trace_events.h,include/asm/ptrace.h,include/asm/irqflags.h,include/asm/preempt.h,include/linux/sched/signal.h,include/linux/pid_namespace.h,include/uapi/linux/ptrace.h,include/linux/elf-em.h,include/asm/socket.h,include/linux/sockios.h,include/linux/uio.h,include/uapi/linux/socket.h,include/linux/restart_block.h,include/asm/thread_info.h,include/linux/rwlock_types.h,include/linux/sysinfo.h,include/linux/kernfs.h,include/uapi/linux/wait.h,include/linux/ktime.h,include/linux/debugobjects.h,include/linux/device.h,include/linux/selinux.h,include/linux/sched/user.h,include/asm/page.h,include/uapi/linux/shm.h,include/asm/shmparam.h,include/uapi/linux/kcov.h,include/linux/timerqueue.h,include/uapi/linux/seccomp.h,include/uapi/linux/resource.h,include/uapi/linux/signal.h,include/linux/bvec.h,include/linux/cgroupstats.h,include/linux/ns_common.h,include/linux/nsproxy.h,include/linux/user_namespace.h,include/linux/kernel_stat.h,include/linux/cgroup-defs.h,include/uapi/linux/sysctl.h,include/linux/math64.h,include/uapi/linux/time.h,include/asm/topology.h,include/generated/bounds.h,include/linux/hash.h,include/uapi/linux/auxvec.h,include/linux/bit_spinlock.h,include/linux/context_tracking_state.h,include/linux/ipc.h,include/asm/sembuf.h,include/linux/tracepoint-defs.h,include/linux/ring_buffer.h,include/linux/trace_seq.h,include/linux/perf_event.h,include/linux/sched/jobctl.h,include/linux/sched/task.h,include/linux/idr.h,include/uapi/linux/uio.h,include/linux/jiffies.h,include/linux/ioport.h,include/linux/klist.h,include/linux/pm.h,include/linux/ratelimit.h,include/asm/device.h,include/linux/pm_wakeup.h,include/asm-generic/hugetlb_encode.h,include/asm/shmbuf.h,include/asm/resource.h,include/asm/signal.h,include/linux/interrupt.h,include/asm/irq.h,include/linux/u64_stats_sync.h,include/linux/bpf-cgroup.h,include/asm/div64.h,include/asm/auxvec.h,include/linux/static_key.h,include/linux/rhashtable.h,include/uapi/linux/ipc.h,include/linux/poll.h,include/linux/seq_buf.h,include/uapi/linux/perf_event.h,include/uapi/linux/bpf_perf_event.h,include/linux/ftrace.h,include/linux/cpu.h,include/linux/irq_work.h,include/linux/jump_label_ratelimit.h,include/linux/perf_regs.h,include/asm/local.h,include/linux/timex.h,include/asm/param.h,include/generated/time.h,include/linux/irqreturn.h,include/linux/irqnr.h,include/asm/sections.h,include/uapi/linux/bpf.h,include/linux/jhash.h,include/linux/list_nulls.h,include/asm/ipcbuf.h,include/uapi/linux/poll.h,include/uapi/linux/eventpoll.h,include/linux/trace_clock.h,include/linux/kallsyms.h,include/asm/ftrace.h,include/linux/node.h,include/linux/cpuhotplug.h,include/linux/sched/task_stack.h,include/uapi/linux/timex.h,include/asm/timex.h,include/uapi/linux/irqnr.h,include/linux/unaligned/packed_struct.h,include/asm/poll.h,include/asm/trace_clock.h,include/linux/magic.h";
        //s = StrUtil.distinct(s);

        String[] str = s.split(",");
        System.out.println(str.length);

        //
        //String pattern = "SUM((\\(?<c>\\(\\)|\\(?<-c>\\)\\)|[^\\(\\)])+)\\(?\\(c\\)\\(?!\\)\\)";
        //String src = "ISNULL(FCommitAmount,0)>=SUM(ISNULL(FAmount,0))+FTaxAmountISNULL(FCommitAmount, 0) >= SUM(FAmount) + FTaxAmount ";
        //Matcher m = Pattern.compile(pattern).matcher(src);
        //while (m.find()) {
        //    String in_exp = m.group().trim();
        //    System.out.println(in_exp + "==================in_exp");
        //}
        /*
        String s = "union {struct pipe_inode_info *i_pipe; struct block_device *i_bdev; struct cdev  *i_cdev; char   *i_link; unsigned  i_dir_seq; };";
        System.out.println(s.matches(Data.unionEnumStructReg));*/
        //String s = "(*ndo_set_rx_headroom)";
        //System.out.println(s.replaceAll("\\(|\\)|\\*", "").trim());
        //s = "int  (*reconnect)(struct serio *)";
        //System.out.println(s.matches(Data.pointerReg));


        //String s = "union {struct pipe_inode_info *i_pipe; struct block_device *i_bdev; struct cdev  *i_cdev; char   *i_link; unsigned  i_dir_seq; };";
        //System.out.println(s.matches(Data.unionEnumStructReg));


        //String p = "long (*dedupe_file_range)(struct file *, long, long, struct file *,long)";
        //System.out.println(p.matches(Data.pointerReg));
        //String s_split = "\\(\\s*\\*\\s*\\b\\w+\\s*\\)\\s*\\("; // (*name)(
        //Matcher m = Pattern.compile(s_split).matcher(p);
        //if (m.find()) {
        //    p = m.group().trim();
        //    p = p.substring(0, p.length() - 1).trim();
        //} else {
        //    p = "no";
        //}

        //System.out.println(p);
        //String s = "static struct static_ const_ enum const,long int,unsigned long int a,int,unsigned int,signed long, enum enum const enum enumm const enum _extern const_ struct task_struct __user *task";
        //s = StrUtil.funcIgnoreNoStar(s);
        //System.out.println(s);


        //String s1;
        //do {
        //    s1 = s;
        //    System.out.println("s1:" + s1);
        //    s = s.replaceAll("\\s+(" + Data.funcIgnoreWord + ")\\s+", " ");
        //    System.out.println("s:" + s);
        //} while (!s.equals(s1));
        //s = s.replaceAll("^(" + Data.funcIgnoreWord + ")\\s+", "");
        //System.out.println(s);


        //String s = ",";
        //System.out.println(s.matches(",+"));


        //String s = "    do_sys_open";
        //int tab = 4;
        //int depth = 1;
        //String reg = "^\\s{" + tab * depth + "}[a-z_]+.*";
        //System.out.println(s.matches(reg));


        //String s = ",,,atomic_t,kuid_t,kgid_t,kuid_t,kgid_t,kuid_t,kgid_t,kuid_t,kgid_t,int,kernel_cap_,kernel_cap_,kernel_cap_,kernel_cap_,kernel_cap_,user_,user_namespace,group_info,rcu_head,";
        //System.out.println(StrUtil.distinct(s));


        //String s = "134";
        //System.out.println(s.split(",").length);

        //List<String> f1_all_inc = new ArrayList<>();
        //f1_all_inc.add("absc");
        //System.out.println(f1_all_inc.contains("absc"));

        //String pointerReg = "^;?[\\w\\s\\*]+\\(\\s*\\*\\s*\\w+\\s*\\)\\s*\\(.*\\);?\\s*$";
        //
        //String body = "void * (*start) (struct seq_file *m, long *pos); void (*stop) (struct seq_file *m, void *v); void * (*next) (struct seq_file *m, void *v, long *pos); int (*show) (struct seq_file *m, void *v);";
        //String[] list = body.split(";");
        //for (String s : list) {
        //    s = s.trim();
        //    if (s.equals("")) continue;
        //    if (s.matches(Data.pointerReg)) { // 处理成员函数
        //        System.out.println("函数指针:" + s);
        //    } else if (s.matches("^\\d+$")) { // 如果是数字，那就是ID，是内部定义的struct/enum/union成员
        //        System.out.println("成员ID:" + s);
        //    } else { // 剩下的就是普通的成员变量，类型可能是Struct或Base，查到ID即可。
        //        System.out.println("普通成员变量:" + s);
        //    }
        //}


        //String s = "struct tree_descr { const char *name; const struct file_operations *ops; int mode; }; ";
        ////System.out.println(s.matches("^.* [_a-zA-Z0-9]+ *\\{.*$"));
        ////s = "PGDE_DATA";
        ////System.out.println(s.matches("^\\s*[_A-Z0-9]+\\(?\\)?"));
        //s = "slab_flags_t kmem_cache_flags(unsigned int object_size, slab_flags_t flags,  char *name, void (*ctor)(void *))";
        //s = "static void * alloc_slabmgmt(struct kmem_cache *cachep, struct page *page, int colour_off, gfp_t local_flags, int nodeid)";
        //
        //String[] tmp = s.substring(0, s.indexOf("(")).replaceAll("\\*", "").trim().split("\\s+");
        //System.out.println(tmp.length);
        //System.out.println(tmp[1]);

        //Matcher m = Pattern.compile("\\b\\w+\\s*\\{").matcher(s);
        //if (m.find()) {
        //    System.out.println(m.group());
        //}


        //
        //String paramFuncReg = "[\\w\\s]+\\(\\*\\w*\\)\\([\\w ,\\*]*\\)"; // 参数表中有两对括号，\\s*表示可能有空白符。非贪婪匹配，尽可能短，加个?。
        //
        //String members_str = "struct super_block *, unsigned long, int (*test)(struct inode *, void *), int (*set)(struct inode *, void *), void *";
        //Matcher m = Pattern.compile(paramFuncReg).matcher(members_str);
        //while (m.find()) {
        //    String f = m.group();
        //    System.out.println(m.start() + "====" + m.end());
        //    System.out.println(members_str.charAt(m.start()) + "..........." + members_str.charAt(m.end()));
        //    List<String> func_split_exp = parseFuncByExp(f);
        //    String name = func_split_exp.get(0);
        //    //存储参数的函数声明： level=15|path=xxx/函数名|name|members_str|father=函数ID
        //
        //    members_str = members_str.replace(f, name);
        //    System.out.println(m.group());
        //}

    }

}
