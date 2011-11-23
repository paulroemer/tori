package org.vaadin.tori.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.tori.ToriApplication;
import org.vaadin.tori.component.HeadingLabel;
import org.vaadin.tori.component.HeadingLabel.HeadingLevel;
import org.vaadin.tori.component.post.PostComponent;
import org.vaadin.tori.data.entity.DiscussionThread;
import org.vaadin.tori.data.entity.Post;
import org.vaadin.tori.mvp.AbstractView;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class ThreadViewImpl extends AbstractView<ThreadView, ThreadPresenter>
        implements ThreadView {

    private CssLayout layout;

    private final Map<Post, PostComponent> postsToComponents = new HashMap<Post, PostComponent>();

    @Override
    protected Component createCompositionRoot() {
        return layout = new CssLayout();
    }

    @Override
    public void initView() {
        layout.setWidth("100%");
    }

    @Override
    protected ThreadPresenter createPresenter() {
        final ToriApplication app = ToriApplication.getCurrent();
        return new ThreadPresenter(app.getDataSource(),
                app.getAuthorizationService());
    }

    @Override
    public DiscussionThread getCurrentThread() {
        return getPresenter().getCurrentThread();
    }

    @Override
    public void displayPosts(final List<Post> posts) {
        layout.removeAllComponents();

        layout.addComponent(new HeadingLabel(getCurrentThread().getTopic(),
                HeadingLevel.H2));

        for (final Post post : posts) {
            final PostComponent c = new PostComponent(post, getPresenter());
            postsToComponents.put(post, c);

            // main component permissions

            if (getPresenter().userMayReportPosts()) {
                c.enableReporting();
            }
            if (getPresenter().userMayEdit(post)) {
                c.enableEditing();
            }
            if (getPresenter().userMayQuote(post)) {
                c.enableQuoting();
            }
            if (getPresenter().userMayVote()) {
                c.enableUpDownVoting(getPresenter().getPostVote(post));
            }

            // context menu permissions

            if (getPresenter().userCanFollowThread()) {
                c.enableThreadFollowing();
            }
            if (getPresenter().userCanUnFollowThread()) {
                c.enableThreadUnFollowing();
            }
            if (getPresenter().userMayBan()) {
                c.enableBanning();
            }
            if (getPresenter().userMayDelete(post)) {
                c.enableDeleting();
            }

            layout.addComponent(c);
        }
    }

    @Override
    public void displayThreadNotFoundError(final String threadIdString) {
        getWindow().showNotification("No thread found for " + threadIdString,
                Notification.TYPE_ERROR_MESSAGE);
    }

    @Override
    protected void navigationTo(final String requestedDataId) {
        super.getPresenter().setCurrentThreadById(requestedDataId);
    }

    @Override
    public void confirmPostReported() {
        getWindow().showNotification("Post is reported!");
    }

    @Override
    public void confirmBanned() {
        getWindow().showNotification("User is banned");
        reloadPage();
    }

    @Override
    public void confirmFollowingThread() {
        getWindow().showNotification("Following thread");
    }

    @Override
    public void confirmUnFollowingThread() {
        getWindow().showNotification("Not following thread anymore");
    }

    @Override
    public void confirmPostDeleted() {
        getWindow().showNotification("Post deleted");
        reloadPage();
    }

    private void reloadPage() {
        displayPosts(getPresenter().getCurrentThread().getPosts());
    }

    @Override
    public void refreshScores(final Post post, final long newScore) {
        postsToComponents.get(post).refreshScores(newScore);
    }
}
