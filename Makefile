jupyter-notebook:
	srun \
		--container-name=romcir22-jupyter \
		--mem=250G -c 50 \
		--container-image=jupyter/datascience-notebook \
		--container-writable \
		--container-mounts=/mnt/ceph/storage/data-tmp/latest/kibi9872/romcir22-keyqueries/:/workspace/,/mnt/ceph/storage/data-in-progress/data-research/web-search/romcir22-keyquery \
		--pty \
		bash -c 'cd /workspace && jupyter notebook --ip 0.0.0.0 --allow-root'

