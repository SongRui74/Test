/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.plugins.preview;

/**
 *
 * @author dell-pc
 */
import org.gephi.graph.api.Graph;
import org.gephi.preview.api.Item;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.spi.ItemBuilder;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = ItemBuilder.class)
public class ItemBuilderTemplate implements ItemBuilder {

    @Override
    public Item[] getItems(Graph graph) {
//        Workspace workspace = graphModel.getWorkspace();
        PreviewProperties properties = Lookup.getDefault().lookup(PreviewController.class).getModel().getProperties();

        if (properties.hasProperty("display-label.node.id")) {
            String nodeId = properties.getStringValue("display-label.node.id");
            return new Item[]{new LabelItem(graph.getNode(nodeId))};
        } else {
            return new Item[0];
        }
    }

    @Override
    public String getType() {
        return "some.type-label";
    }
}
