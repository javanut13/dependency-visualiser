let depColor = (count) => {
  if (count > 10) {
    return '#da4939';
  } else if (count > 5) {
    return '#cc7833';
  } else if (count > 0){
    return '#6d9cbe';
  } else {
    return '#519f50';
  }
};

let typeColor = (type) => {
  switch (type) {
    case "class": return '#f4f1ed';
    case "interface": return '#d4cfc9';
    case "enum": return '#5a647e';
    case "object": return '#ffc66d';
    default: return '';
  }
};

window.loaded_data = (data) => {
  let nodes = [];
  let edges = [];
  for (let key in data) {
    let info = data[key];
    let dependent = info.symbols;
    let node = {
      id: key,
      label: key,
      shape: 'box'
    };
    let count = 0;
    for (let idx in dependent) {
      let dep = dependent[idx];
      if (key !== dep && data[dep]) {
        edges.push({
          from: key,
          to: dep
        });
        count += 1;
      }
    }

    node.color = {
      background: typeColor(info.type),
      border: depColor(count)
    };
    nodes.push(node);
  }

  // create a network
  var container = document.getElementById('network');
  var data = {
    nodes: new vis.DataSet(nodes),
    edges: new vis.DataSet(edges)
  };
  var options = {
    layout: {
      improvedLayout: false
    },
    edges: {
      arrows: {
        to: true
      },
      length: 200
    },
    nodes: {
      borderWidth: 2
    },
    physics: {
      enabled: true
    }
  };
  var network = new vis.Network(container, data, options);
  console.log("Shown");
 
};
